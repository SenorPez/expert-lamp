package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

public class Material {
    private final int id;
    private final String name;
    private final Set<Integer> itemIds = new HashSet<>();

    public Material(ObjectNode json) {
        this.id = json.get("id").asInt();
        this.name = json.get("name").asText();
        json.get("items").forEach(item -> this.itemIds.add(item.asInt()));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Integer> getItemIds() {
        return itemIds;
    }
}
