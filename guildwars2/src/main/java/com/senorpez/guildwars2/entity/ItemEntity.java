package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.Item;

import javax.persistence.*;

@Entity
@Table(name = "items")
public class ItemEntity {
    @Id
    private int id;
    private String name;

    @OneToOne(mappedBy = "item")
    @JoinColumn(name = "item_id")
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaterialEntity getMaterial() {
        return material;
    }

    public void setMaterial(MaterialEntity material) {
        this.material = material;
    }
}
