package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static com.senorpez.guildwars2.api.RandomString.generateString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {
    ObjectNode json;
    Random random = new Random();

    int expectedId;
    String expectedName;
    int expectedVendorValue;
    Set<String> expectedFlags;

    @BeforeEach
    void init() {
        expectedId = random.nextInt();
        expectedName = generateString;
        expectedVendorValue = random.nextInt();
        IntStream.range(0, 16).forEach(i -> expectedFlags.add(generateString));

        ObjectMapper mapper = new ObjectMapper();
        json = mapper.createObjectNode();
        json.put("id", expectedId);
        json.put("name", expectedName);
        json.put("vendor_value", expectedVendorValue);

        ArrayNode flagsArrayNode = mapper.createArrayNode();
        expectedFlags.forEach(flagsArrayNode::add);
        json.set("flags", flagsArrayNode);
    }

    @Test
    void getId() {
        Item instance = new Item(json);
        assertEquals(instance.getId(), expectedId);
    }

    @Test
    void getName() {
        Item instance = new Item(json);
        assertEquals(instance.getName(), expectedName);
    }

    @Test
    void getVendorValue() {
    }

    @Test
    void getFlags() {
    }
}

