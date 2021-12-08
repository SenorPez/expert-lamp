package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.ItemBuilder;
import com.senorpez.guildwars2.api.Material;
import com.senorpez.guildwars2.api.MaterialBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "materials")
public class MaterialEntity {
    @Id
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "material")
    private Set<ItemEntity> items = new HashSet<>();

    public MaterialEntity() {
    }

    public MaterialEntity(Material material, Session session) throws IOException {
        this.id = material.getId();
        this.name = material.getName();

        Set<Integer> materialItemIds = material.getItemIds();

        // Get existing database items
        String hql = "FROM ItemEntity WHERE id IN :ids";
        TypedQuery<ItemEntity> query = session.createQuery(hql, ItemEntity.class);
        query.setParameter("ids", materialItemIds);
        Set<ItemEntity> databaseItems = new HashSet<>(query.getResultList());
        this.items.addAll(databaseItems);

        Set<Integer> databaseItemIds = databaseItems
                .stream()
                .map(ItemEntity::getId)
                .collect(Collectors.toSet());

        // Find and create items missing from database.
        Set<Integer> missingItemIds = setDifference(materialItemIds, databaseItemIds);
        if (missingItemIds.size() > 0) {
            ItemBuilder itemBuilder = new ItemBuilder();
            Set<ItemEntity> apiResults = itemBuilder
                    .get(materialItemIds.stream())
                    .map(ItemEntity::new)
                    .collect(Collectors.toSet());
            this.items.addAll(apiResults);
        }

        // Set this material to the item material.
        this.items.forEach(itemEntity -> {
            itemEntity.setMaterial(this);
            session.merge(itemEntity);
        });
    }

    public int getId() {
        return id;
    }

    public MaterialEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MaterialEntity setName(String name) {
        this.name = name;
        return this;
    }

    public Set<ItemEntity> getItems() {
        return items;
    }

    public MaterialEntity setItems(Set<ItemEntity> items) {
        this.items = items;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final MaterialEntity that)) return false;
        return that.getId() == getId();
    }

    @Override
    public int hashCode() {
        return ((Integer) getId()).hashCode();
    }

    private Set<Integer> setDifference(Set<Integer> b, Set<Integer> a) {
        // Returns B \ A, such that {x in B, x not in A}
        Set<Integer> e = new HashSet<>(b);
        e.removeAll(a);
        return e;
    }

    public static void updateAllMaterials(Session session) throws IOException, URISyntaxException, InterruptedException, ExecutionException {
        MaterialBuilder builder = new MaterialBuilder();
        Transaction tx = session.beginTransaction();
        Stream<MaterialEntity> materialEntities = builder.get()
                .map(material -> {
                    try {
                        return new MaterialEntity(material, session);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
        materialEntities.forEach(session::merge);
        tx.commit();
    }
}
