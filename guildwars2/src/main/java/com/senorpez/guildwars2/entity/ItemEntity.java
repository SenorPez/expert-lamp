package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.Item;

import javax.persistence.*;

@Entity
@Table(name = "items")
public class ItemEntity {
    @Id
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "material_id", updatable = false)
    private MaterialEntity material;

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
}
