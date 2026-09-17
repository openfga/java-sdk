package dev.openfga.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpServer;
import dev.openfga.sdk.api.BaseStreamingApi;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.model.StreamResult;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class LegacyStreamingApiTest {
    @Test
    void preservesLegacySubclassParsingAndStreaming() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/stream", exchange -> {
            byte[] body = "{\"result\":[\"document:one\",\"document:two\"]}\n".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        try {
            LegacyStreamingApi api = new LegacyStreamingApi(new ApiClient());
            assertEquals(List.of("document:one"), api.parse("{\"result\":[\"document:one\"]}"));
            List<List<String>> results = new ArrayList<>();
            api.stream(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/stream"), results::add)
                    .get(5, TimeUnit.SECONDS);
            assertEquals(List.of(List.of("document:one", "document:two")), results);
        } finally {
            server.stop(0);
        }
    }

    @SuppressWarnings("deprecation")
    static class LegacyStreamingApi extends BaseStreamingApi<List<String>> {
        LegacyStreamingApi(ApiClient client) {
            super(new Configuration(), client, new TypeReference<StreamResult<List<String>>>() {});
        }

        List<String> parse(String source) throws Exception {
            return objectMapper.readValue(source, streamResultTypeRef).getResult();
        }

        CompletableFuture<Void> stream(URI uri, Consumer<List<String>> consumer) {
            return processStreamingResponse(HttpRequest.newBuilder(uri).build(), consumer, null);
        }
    }
}
