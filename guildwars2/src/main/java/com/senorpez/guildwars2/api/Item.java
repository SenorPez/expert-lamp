package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

public class Item {
    private final int id;
    private final String name;
    private final int vendorValue;
    private final Set<String> flags = new HashSet<>();

    public Item(ObjectNode json) {
        this.id = json.get("id").asInt();
        this.name = json.get("name").asText();
        this.vendorValue = json.get("vendor_value").asInt();
        json.get("flags")
                .elements()
                .forEachRemaining(node -> this.flags.add(node.asText()));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVendorValue() {
        return vendorValue;
    }

    public Set<String> getFlags() {
        return flags;
    }
}
