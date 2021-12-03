package com.senorpez.guildwars2.api.commerce;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Price {
    private final int id;
    private final int buyQuantity;
    private final int buyPrice;
    private final int sellQuantity;
    private final int sellPrice;

    public Price(ObjectNode json) {
        this.id = json.get("id").asInt();

        final ObjectNode buy = (ObjectNode) json.get("buys");
        final ObjectNode sell = (ObjectNode) json.get("sells");

        this.buyQuantity = buy.get("quantity").asInt();
        this.buyPrice = buy.get("unit_price").asInt();
        this.sellQuantity = sell.get("quantity").asInt();
        this.sellPrice = sell.get("unit_price").asInt();
    }

    public int getId() {
        return id;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public int getSellPrice() {
        return sellPrice;
    }
}
