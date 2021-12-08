package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.Item;
import com.senorpez.guildwars2.api.ItemBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Entity
@Table(name = "items")
public class ItemEntity {
    @Id
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @OneToMany(mappedBy = "item")
    private Set<PriceEntity> prices = new HashSet<>();


    public ItemEntity() {
    }

    public ItemEntity(Item item) {
        this.id = item.getId();
        this.name = item.getName();
    }

    public int getId() {
        return id;
    }

    public ItemEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemEntity setName(String name) {
        this.name = name;
        return this;
    }

    public MaterialEntity getMaterial() {
        return material;
    }

    public ItemEntity setMaterial(MaterialEntity material) {
        this.material = material;
        return this;
    }

    public Set<PriceEntity> getPrices() {
        return prices;
    }

    public ItemEntity setPrices(Set<PriceEntity> prices) {
        this.prices = prices;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final ItemEntity that)) return false;
        return that.getId() == getId();
    }

    @Override
    public int hashCode() {
        return ((Integer) getId()).hashCode();
    }

    public static void updateAllItems(Session session) throws IOException, URISyntaxException, InterruptedException, ExecutionException {
        ItemBuilder builder = new ItemBuilder();
        Stream<ItemEntity> itemEntities = builder.get()
                .map(ItemEntity::new);

        Transaction tx = session.beginTransaction();
        AtomicInteger count = new AtomicInteger(0);
        itemEntities.forEach(itemEntity -> {
            ItemEntity existing = session.get(ItemEntity.class, itemEntity.getId());
            if (existing != null) itemEntity.setMaterial(existing.getMaterial());
            session.merge(itemEntity);
            if (count.incrementAndGet() % 50 == 0) {
                session.flush();
                session.clear();
            }
        });
        tx.commit();
    }
}
