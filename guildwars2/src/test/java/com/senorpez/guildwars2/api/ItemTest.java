package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {
    ObjectNode json;
    Random random = new Random();

    int expectedId;
    String expectedName;

    @BeforeEach
    void init() {
        expectedId = random.nextInt();
        expectedName = generateString();
        ObjectMapper mapper = new ObjectMapper();
        json = mapper.createObjectNode();

        json.put("id", expectedId);
        json.put("name", expectedName);
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

    private String generateString() {
        return random.ints(97, 123)
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

