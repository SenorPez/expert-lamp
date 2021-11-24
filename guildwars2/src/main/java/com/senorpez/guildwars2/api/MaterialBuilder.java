package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Function;
import java.util.stream.Stream;

public class MaterialBuilder extends APIObjectBuilder<Material> {
    private final static Function<Stream<ObjectNode>, Stream<Material>> creator = node -> node.map(Material::new);
    private final static Endpoint endpoint = new Endpoint("/v2/materials");

    public MaterialBuilder() {
        super(creator, endpoint);
    }
}
