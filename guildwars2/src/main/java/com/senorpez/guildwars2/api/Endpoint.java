package com.senorpez.guildwars2.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
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

    public CompletableFuture<String> getPopcorn() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Popcorn started");
            Random r = new Random();
            try {
                TimeUnit.SECONDS.sleep(r.nextInt(3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Popcorn");

            return "Popcorn ready";
        });
    }

    public CompletableFuture<String> getDrink() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Drink started");
            Random r = new Random();
            try {
                TimeUnit.SECONDS.sleep(r.nextInt(3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Drink");

            return "Drink ready";
        });
    }

    public String snackReady() {
        return "Order is ready";
    }

    public void newGetAll() throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(protocol, host, resource, "page=0&page_size=200", null))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        final int totalPages = (int) response.headers().firstValueAsLong("X-Page-Total").orElse(0L);

        List<CompletableFuture<Stream<ObjectNode>>> lottaGets = new ArrayList<>();
        for (int currentPage = 0; currentPage < totalPages; currentPage++) {
            lottaGets.add(getAPIResult(currentPage).thenApplyAsync(toJson()));
            System.out.printf("%d %d\n", currentPage, totalPages);
        }

        CompletableFuture
                .allOf(lottaGets.toArray(new CompletableFuture[0]))
                .thenRun(() -> System.out.println("All done"))
                .get();



//        CompletableFuture<String> page0 = CompletableFuture.supplyAsync(getAPIResult(0));
//        CompletableFuture<Stream<ObjectNode>> page0 = getAPIResult(0).thenApplyAsync(toJson());
//        CompletableFuture<Stream<ObjectNode>> page1 = getAPIResult(1).thenApplyAsync(toJson());
//        CompletableFuture.allOf(page0, page1).thenRunAsync(() -> {
//            System.out.println("Both done");
//            try {
//                System.out.println(page0.get().collect(Collectors.toList()));
//                System.out.println(page1.get().collect(Collectors.toList()));
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }).get();
//        page0.thenAcceptAsync(s -> System.out.println("Done"));
//        page0.thenAcceptAsync(s -> System.out.println("Thing"));
    }

//    public Supplier<String> getAPIResult(final int page) {
//        return () -> {
//            System.out.printf("HERE %d\n", page);
//            HttpResponse<String> response = getAPIResponse(page);
//            System.out.printf("THERE %d\n", page);
//            return response != null ? response.body() : null;
//        };
//    }

    private Function<String, Stream<ObjectNode>> toJson() {
        return result -> {
            Stream.Builder<ObjectNode> builder = Stream.builder();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Optional<ArrayNode> array = Optional.of((ArrayNode) objectMapper.readTree(result));
                array.ifPresent(json -> json.forEach(item -> builder.accept((ObjectNode) item)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return builder.build();
        };
    }

    public CompletableFuture<String> getAPIResult(final int page) {
        return CompletableFuture
                .supplyAsync(() -> {
                    System.out.printf("HERE %d\n", page);
                    HttpResponse<String> response = getAPIResponse(page);
                    System.out.printf("THERE %d\n", page);
                    return response != null ? response.body() : null;
                });
    }

    private HttpResponse<String> getAPIResponse(final int page) {
        System.out.printf("Started %d\n", page);
        final String queryString = "page=%d&page_size=200";

        int attempt = 0;
        final int maxAttempts = 5;
        boolean done = false;

        HttpResponse<String> response = null;

        try {
            while (!done) {
                attempt++;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(protocol, host, resource, String.format(queryString, page), null))
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
        System.out.printf("Finished %d\n", page);
        return response;
    }

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        Endpoint endpoint = new Endpoint("/v2/items");
        endpoint.newGetAll();
    }

//    public Stream<ObjectNode> newGetAll() throws URISyntaxException, IOException, InterruptedException {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(protocol, host, resource, "page=0&page_size=200"))
//                .GET()
//                .build();
//        HttpResponse<String> response = HttpClient.newBuilder()
//                .build()
//                .send(request, HttpResponse.BodyHandlers.ofString());
//
//        int totalPages = (int) response.headers().firstValueAsLong("X-Page-Total").orElse(0L);
//        int currentPage = 1;
//        getApiResponse().get();
//    }
//
//    private CompletableFuture<HttpResponse<String>> getApiResponse() {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(new URI(protocol, host, resource, "page=0&page_size=200"))
//                        .GET()
//                        .build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        })
//    }

//    private CompletableFuture<HttpResponse<String>> getApiResponse() {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(new URI(protocol, host, resource, "page=0&page_size=200"))
//                        .GET()
//                        .build();
//                return HttpClient.newBuilder()
//                        .build()
//                        .send(request, HttpResponse.BodyHandlers.ofString());
//            } catch (URISyntaxException | InterruptedException | IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        });
//    }

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
