package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.senorpez.guildwars2.api.RandomString.generateString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class MaterialTest {
    ObjectNode json;
    Random random = new Random();

    int expectedId;
    String expectedName;
    Set<Integer> expectedItemIds;

    @BeforeEach
    void init() {
        expectedId = random.nextInt();
        expectedName = generateString;
        expectedItemIds = IntStream
                .range(0, random.nextInt(16) + 2)
                .map(i -> random.nextInt())
                .boxed()
                .collect(Collectors.toSet());

        ObjectMapper mapper = new ObjectMapper();
        json = mapper.createObjectNode();
        json.put("id", expectedId);
        json.put("name", expectedName);

        ArrayNode itemIds = mapper.createArrayNode();
        expectedItemIds.forEach(itemIds::add);
        json.set("items", itemIds);
    }

    @Test
    void getId() {
        Material instance = new Material(json);
        assertEquals(instance.getId(), expectedId);
    }

    @Test
    void getName() {
        Material instance = new Material(json);
        assertEquals(instance.getName(), expectedName);
    }

    @Test
    void getItemIds() {
        Material instance = new Material(json);
        assertIterableEquals(instance.getItemIds(), expectedItemIds);
    }
}