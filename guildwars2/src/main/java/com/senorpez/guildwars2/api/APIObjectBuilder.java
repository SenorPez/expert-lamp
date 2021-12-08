package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

abstract public class APIObjectBuilder<T> {
    private final Function<Stream<ObjectNode>, Stream<T>> creator;
    private final Endpoint endpoint;

    public APIObjectBuilder(Function<Stream<ObjectNode>, Stream<T>> creator, Endpoint endpoint) {
        this.creator = creator;
        this.endpoint = endpoint;
    }

    public Stream<T> get(int id) throws IOException {
        Stream<ObjectNode> json = endpoint.getSingle(id);
        return creator.apply(json);
    }

    public Stream<T> get(Stream<Integer> ids) throws IOException {
        Stream<ObjectNode> json = endpoint.getMultiple(ids);
        return creator.apply(json);
    }

    public Stream<T> get() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
        Stream<ObjectNode> json = endpoint.getAll();
        return creator.apply(json);
    }
}
