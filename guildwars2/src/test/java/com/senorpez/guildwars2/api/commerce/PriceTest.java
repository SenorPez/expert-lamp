package com.senorpez.guildwars2.api.commerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceTest {
    ObjectNode json;
    Random random = new Random();

    int expectedId;
    int expectedBuyQuantity;
    int expectedBuyPrice;
    int expectedSellQuantity;
    int expectedSellPrice;

    @BeforeEach
    void init() {
        expectedId = random.nextInt();
        expectedBuyQuantity = random.nextInt();
        expectedBuyPrice = random.nextInt();
        expectedSellQuantity = random.nextInt();
        expectedSellPrice = random.nextInt();

        ObjectMapper mapper = new ObjectMapper();
        json = mapper.createObjectNode();
        json.put("id", expectedId);

        ObjectNode buyNode = mapper.createObjectNode();
        buyNode.put("quantity", expectedBuyQuantity);
        buyNode.put("unit_price", expectedBuyPrice);
        json.set("buys", buyNode);

        ObjectNode sellNode = mapper.createObjectNode();
        sellNode.put("quantity", expectedSellQuantity);
        sellNode.put("unit_price", expectedSellPrice);
        json.set("sells", sellNode);
    }

    @Test
    void getId() {
        Price instance = new Price(json);
        assertEquals(instance.getId(), expectedId);
    }

    @Test
    void getBuyQuantity() {
        Price instance = new Price(json);
        assertEquals(instance.getBuyPrice(), expectedBuyPrice);
    }

    @Test
    void getBuyPrice() {
        Price instance = new Price(json);
        assertEquals(instance.getBuyQuantity(), expectedBuyQuantity);
    }

    @Test
    void getSellQuantity() {
        Price instance = new Price(json);
        assertEquals(instance.getSellQuantity(), expectedSellQuantity);
    }

    @Test
    void getSellPrice() {
        Price instance = new Price(json);
        assertEquals(instance.getSellPrice(), expectedSellPrice);
    }
}