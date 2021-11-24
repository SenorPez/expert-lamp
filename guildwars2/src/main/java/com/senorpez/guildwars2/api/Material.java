package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Material {
    private final int id;
    private final String name;
    private final List<Integer> itemIds = new ArrayList<>();

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

    public List<Integer> getItemIds() {
        return itemIds;
    }
}
