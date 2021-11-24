package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Function;
import java.util.stream.Stream;

public class ItemBuilder extends APIObjectBuilder<Item> {
    private final static Function<Stream<ObjectNode>, Stream<Item>> creator = node -> node.map(Item::new);
    private final static Endpoint endpoint = new Endpoint("/v2/items");

    public ItemBuilder() {
        super(creator, endpoint);
    }
}
