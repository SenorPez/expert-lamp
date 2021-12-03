package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Endpoint {
    private final String protocol = "https";
    private final String host = "api.guildwars2.com";
    private final String resource;

    public Endpoint(String resource) {
        this.resource = resource;
    }

    public Stream<ObjectNode> getSingle(int id) throws IOException {
        return getMultiple(Stream.of(id));
    }

    public Stream<ObjectNode> getMultiple(Stream<Integer> ids) throws IOException {
        Stream<Stream<Integer>> chunks = chunkIds(ids);
        Stream<Stream<ObjectNode>> objectChunks = chunks.map(chunk -> {
            String params = "?ids=" + chunk.map(Object::toString).collect(Collectors.joining(","));
            Stream.Builder<ObjectNode> builder = Stream.builder();
            try {
                Optional<ArrayNode> jsonArray = callAPI(params);
                jsonArray.ifPresent(json -> json.forEach(item -> builder.accept((ObjectNode) item)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.build();
        });
        return objectChunks.reduce(Stream::concat).orElseThrow(IOException::new);
    }

    public Stream<ObjectNode> getAll() throws IOException {
        return getMultiple(getAllIds());
    }

    private Stream<Integer> getAllIds() throws IOException {
        Optional<ArrayNode> ids = callAPI("");
        Stream.Builder<Integer> integerBuilder = Stream.builder();
        ids.ifPresent(jsonNodes -> jsonNodes
                .forEach(node -> integerBuilder.accept(node.asInt())));
        return integerBuilder.build();
    }

    private Optional<ArrayNode> callAPI(String params) throws IOException {
        URL url = new URL(protocol, host, resource + params);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200 || con.getResponseCode() == 206) {
            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of((ArrayNode) objectMapper.readTree(con.getInputStream()));
        } else if (con.getResponseCode() == 404) {
            return Optional.empty();
        } else {
            throw new IOException(String.valueOf(con.getResponseCode()));
        }
    }

    private Stream<Stream<Integer>> chunkIds(Stream<Integer> itemIds) {
        LinkedList<Integer> list = itemIds.collect(Collectors.toCollection(LinkedList::new));
        Stream.Builder<Stream<Integer>> returnValue = Stream.builder();
        int baseLength = (protocol + "://" + host + resource + "?=ids").length();

        while (list.peek() != null) {
            Stream.Builder<Integer> builder = Stream.builder();
            int paramsLength = 0;

            while (list.peek() != null && baseLength + paramsLength + list.peek().toString().length() + 1 < 255) {
                builder.accept(list.peek());
                paramsLength += list.remove().toString().length() + 1;
            }
            returnValue.accept(builder.build());
        }
        return returnValue.build();
    }
}
