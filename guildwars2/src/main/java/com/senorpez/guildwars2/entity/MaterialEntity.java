package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.ItemBuilder;
import com.senorpez.guildwars2.api.Material;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "materials")
public class MaterialEntity {
    @Id
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "material_id")
    private Set<ItemEntity> items = new HashSet<>();

    public MaterialEntity() {
    }

    public MaterialEntity(Material material) {
        this.id = material.getId();
        this.name = material.getName();

        try {
            ItemBuilder itemBuilder = new ItemBuilder();
            this.items = itemBuilder.get(material.getItemIds().stream())
                    .map(ItemEntity::new)
                    .map(itemEntity -> itemEntity.setMaterial(this))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
