package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Material {
    private final static String protocol = "https";
    private final static String host = "api.guildwars2.com";
    private final static String resource = "/v2/materials";

    private final int id;
    private final String name;
    private final List<Integer> itemIds = new ArrayList<>();

    public Material(ObjectNode materialJson) {
        this.id = materialJson.get("id").asInt();
        this.name = materialJson.get("name").asText();
        materialJson.get("items").forEach(item -> this.itemIds.add(item.asInt()));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public static Material getSingle(int materialId) throws IOException {
        String params = "?ids=" + materialId;
        Stream<Material> materials = Material.callAPI(params);
        return materials.findFirst().orElseThrow(IOException::new);
    }

    public static Stream<Material> getMultiple(Stream<Integer> itemIds) throws IOException {
        Stream<Stream<Integer>> chunks = chunkIds(itemIds);
        Stream<Stream<Material>> materialChunks = chunks.map(chunk -> {
            String params = "?ids=" + chunk.map(Object::toString).collect(Collectors.joining(","));
            try {
                return Material.callAPI(params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        return materialChunks.reduce(Stream::concat).orElse(Stream.of());
    }

    public static Stream<Material> getAll() throws IOException {
        URL url = new URL(Material.protocol, Material.host, Material.resource);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode json = (ArrayNode) objectMapper.readTree(con.getInputStream());
            Stream.Builder<Integer> integerBuilder = Stream.builder();
            json.forEach(node -> integerBuilder.accept(node.asInt()));

            Stream<Stream<Integer>> chunks = chunkIds(integerBuilder.build());
            Stream<Stream<Material>> materialChunks = chunks.map(chunk -> {
                try {
                    return getMultiple(chunk);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });
            return materialChunks.reduce(Stream::concat).orElse(Stream.of());
        } else {
            throw new IOException(String.valueOf(con.getResponseCode()));
        }
    }

    private static Stream<Material> callAPI(String params) throws IOException {
        URL url = new URL(Material.protocol, Material.host, Material.resource + params);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode json = (ArrayNode) objectMapper.readTree(con.getInputStream());
            Stream.Builder<Material> materialBuilder = Stream.builder();
            json.forEach(node -> materialBuilder.accept(new Material((ObjectNode) node)));
            return materialBuilder.build();
        } else {
            throw new IOException(String.valueOf(con.getResponseCode()));
        }
    }

    private static Stream<Stream<Integer>> chunkIds(Stream<Integer> itemIds) {
        LinkedList<Integer> list = itemIds.collect(Collectors.toCollection(LinkedList::new));
        Stream.Builder<Stream<Integer>> returnValue = Stream.builder();

        int baseLength = (Material.protocol + "://" + Material.host + Material.resource + "?=ids").length();

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
