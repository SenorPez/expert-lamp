package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.Material;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "materials")
public class MaterialEntity {
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @Id
    @Column(name = "item_id", nullable = false)
    private int itemId;

    @OneToOne(optional = false)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    public MaterialEntity() {
    }

    public MaterialEntity(int id, String name, int itemId) {
        this.id = id;
        this.name = name;
        this.itemId = itemId;
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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public static List<MaterialEntity> buildEntities(Material material) {
        return material.getItemIds()
                .stream()
                .map(itemId -> new MaterialEntity(material.getId(), material.getName(), itemId))
                .collect(Collectors.toList());
    }
}
