package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Endpoint {
    private final String protocol = "https";
    private final String host = "api.guildwars2.com";
    private final String resource;

    private final BiFunction<Integer, String, HttpResponse<String>> functionGetResponse = (page, apiResource) -> {
        final String queryString = "page=%d&page_size=200";

        int attempt = 0;
        final int maxAttempts = 5;
        boolean done = false;

        HttpResponse<String> response = null;

        try {
            while (!done) {
                attempt++;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(protocol, host, apiResource, String.format(queryString, page), null))
                        .GET()
                        .build();
                response = HttpClient.newBuilder()
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    done = true;
                } else if (response.statusCode() == 429 && attempt < maxAttempts) {
                    System.out.printf("HTTP Status 429 for page %d; Pausing for 90 seconds.", page);
                    TimeUnit.SECONDS.sleep(90);
                    System.out.printf("Retrying page %d", page);
                } else {
                    throw new IOException(String.format("HTTP Status %d", response.statusCode()));
                }
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    };
    private final Function<HttpResponse<String>, String> functionGetResponseBody = response -> response != null ? response.body() : null;
    private final Function<String, Stream<ObjectNode>> functionToJSON = responseBody -> {
        Stream.Builder<ObjectNode> builder = Stream.builder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Optional<ArrayNode> array = Optional.of((ArrayNode) objectMapper.readTree(responseBody));
            array.ifPresent(json -> json.forEach(item -> builder.accept((ObjectNode) item)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return builder.build();
    };

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

    public Stream<ObjectNode> getAll() throws InterruptedException, URISyntaxException, IOException, ExecutionException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(protocol, host, resource, "page=0&page_size=200", null))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new IOException(String.format("HTTP Error %d; Aborting", response.statusCode()));
        final int totalPages = (int) response.headers().firstValueAsLong("X-Page-Total").orElse(0L);

        List<CompletableFuture<Stream<ObjectNode>>> getRequests = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);

        IntStream.range(0, totalPages).forEach(pageNumber -> {
            CompletableFuture<Stream<ObjectNode>> completableFuture = CompletableFuture
                    .completedFuture(pageNumber)
                    .thenApplyAsync(page -> functionGetResponse.apply(page, this.resource), executor)
                    .thenApplyAsync(functionGetResponseBody, executor)
                    .thenApplyAsync(functionToJSON, executor);
            getRequests.add(completableFuture);
        });

        CompletableFuture
                .allOf(getRequests.toArray(new CompletableFuture[0]))
                .thenRun(() -> System.out.println("Complete"))
                .get();

        Stream.Builder<ObjectNode> builder = Stream.builder();
        getRequests.forEach(item -> {
            try {
                System.out.println(item);
                item.get().forEach(builder);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return builder.build();
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
