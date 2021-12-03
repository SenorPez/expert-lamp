package com.senorpez.guildwars2.api.commerce;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.senorpez.guildwars2.api.APIObjectBuilder;
import com.senorpez.guildwars2.api.Endpoint;

import java.util.function.Function;
import java.util.stream.Stream;

public class PriceBuilder extends APIObjectBuilder<Price> {
    private final static Function<Stream<ObjectNode>, Stream<Price>> creator = node -> node.map(Price::new);
    private final static Endpoint endpoint = new Endpoint("/v2/commerce/prices");

    public PriceBuilder() {
        super(creator, endpoint);
    }
}
