package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Item {
    private final int id;
    private final String name;
    private final int vendorValue;

    public Item(ObjectNode json) {
        this.id = json.get("id").asInt();
        this.name = json.get("name").asText();
        this.vendorValue = json.get("vendor_value").asInt();
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
}
