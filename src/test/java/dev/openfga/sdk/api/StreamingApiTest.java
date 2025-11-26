package dev.openfga.sdk.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.model.ListObjectsRequest;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class demonstrating the generic streaming implementation.
 * This test shows how the BaseStreamingApi provides reusable functionality
 * for any streaming endpoint that returns line-delimited JSON format.
 */
class StreamingApiTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<Stream<String>> mockHttpResponse;

    @Mock
    private Configuration mockConfiguration;

    @Mock
    private ApiClient mockApiClient;

    private StreamedListObjectsApi streamingApi;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        when(mockApiClient.getObjectMapper()).thenReturn(objectMapper);
        when(mockApiClient.getHttpClient()).thenReturn(mockHttpClient);

        when(mockConfiguration.getApiUrl()).thenReturn("https://api.fga.example");
        when(mockConfiguration.getReadTimeout()).thenReturn(Duration.ofSeconds(10));
        when(mockConfiguration.getCredentials()).thenReturn(null);
        when(mockConfiguration.override(any())).thenReturn(mockConfiguration);

        streamingApi = new StreamedListObjectsApi(mockConfiguration, mockApiClient);
    }

    @Test
    void testStreamedListObjects_successfulStream() throws Exception {
        // Arrange: Create response with multiple objects
        String resp = "{\"result\":{\"object\":\"document:1\"}}\n"
                + "{\"result\":{\"object\":\"document:2\"}}\n"
                + "{\"result\":{\"object\":\"document:3\"}}\n";

        Stream<String> lineStream = resp.lines();
        when(mockHttpResponse.body()).thenReturn(lineStream);
        when(mockHttpResponse.statusCode()).thenReturn(200);

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        // Act: Collect streamed objects
        List<String> receivedObjects = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(3);

        ListObjectsRequest request =
                new ListObjectsRequest().type("document").relation("viewer").user("user:anne");

        CompletableFuture<Void> future = streamingApi.streamedListObjects("store123", request, response -> {
            receivedObjects.add(response.getObject());
            latch.countDown();
        });

        // Assert: Wait for completion and verify results
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Should receive all objects");
        future.join();

        assertEquals(3, receivedObjects.size());
        assertEquals("document:1", receivedObjects.get(0));
        assertEquals("document:2", receivedObjects.get(1));
        assertEquals("document:3", receivedObjects.get(2));
    }

    @Test
    void testStreamedListObjects_withErrorInStream() throws Exception {
        // Arrange: Streaming response with an error
        String resp = "{\"result\":{\"object\":\"document:1\"}}\n"
                + "{\"error\":{\"code\":400,\"message\":\"Something went wrong\"}}\n";

        Stream<String> lineStream = resp.lines();
        when(mockHttpResponse.body()).thenReturn(lineStream);
        when(mockHttpResponse.statusCode()).thenReturn(200);

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        // Act: Collect objects and errors
        List<String> receivedObjects = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> receivedErrors = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        ListObjectsRequest request =
                new ListObjectsRequest().type("document").relation("viewer").user("user:anne");

        CompletableFuture<Void> future = streamingApi.streamedListObjects(
                "store123", request, response -> receivedObjects.add(response.getObject()), error -> {
                    receivedErrors.add(error);
                    latch.countDown();
                });

        // Assert: Should receive one object and one error
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Should receive error");
        future.join();

        assertEquals(1, receivedObjects.size());
        assertEquals("document:1", receivedObjects.get(0));
        assertEquals(1, receivedErrors.size());
        assertTrue(receivedErrors.get(0).getMessage().contains("Something went wrong"));
    }

    @Test
    void testStreamedListObjects_httpError() throws Exception {
        // Arrange: HTTP error response
        when(mockHttpResponse.statusCode()).thenReturn(400);
        when(mockHttpResponse.headers())
                .thenReturn(java.net.http.HttpHeaders.of(Collections.emptyMap(), (k, v) -> true));

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        // Act: Try to stream
        List<String> receivedObjects = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean errorReceived = new AtomicBoolean(false);

        ListObjectsRequest request =
                new ListObjectsRequest().type("document").relation("viewer").user("user:anne");

        CompletableFuture<Void> future = streamingApi.streamedListObjects(
                "store123",
                request,
                response -> receivedObjects.add(response.getObject()),
                error -> errorReceived.set(true));

        // Assert: Should fail with ApiException
        assertThrows(Exception.class, future::join);
        assertTrue(errorReceived.get(), "Error consumer should be called");
        assertEquals(0, receivedObjects.size());
    }

    @Test
    void testStreamedListObjects_emptyStream() throws Exception {
        String streamResponse = "";

        Stream<String> lineStream = streamResponse.lines();
        when(mockHttpResponse.body()).thenReturn(lineStream);
        when(mockHttpResponse.statusCode()).thenReturn(200);

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        // Act: Stream with no results
        List<String> receivedObjects = Collections.synchronizedList(new ArrayList<>());

        ListObjectsRequest request =
                new ListObjectsRequest().type("document").relation("viewer").user("user:anne");

        CompletableFuture<Void> future = streamingApi.streamedListObjects(
                "store123", request, response -> receivedObjects.add(response.getObject()));

        // Assert: Should complete without errors
        future.join();
        assertEquals(0, receivedObjects.size());
    }

    @Test
    void testStreamedListObjects_largeStream() throws Exception {
        // Arrange: Large streaming response with many objects
        StringBuilder streamBuilder = new StringBuilder();
        int objectCount = 1000;
        for (int i = 0; i < objectCount; i++) {
            streamBuilder
                    .append("{\"result\":{\"object\":\"document:")
                    .append(i)
                    .append("\"}}\n");
        }

        Stream<String> lineStream = streamBuilder.toString().lines();
        when(mockHttpResponse.body()).thenReturn(lineStream);
        when(mockHttpResponse.statusCode()).thenReturn(200);

        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        List<String> receivedObjects = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(objectCount);

        ListObjectsRequest request =
                new ListObjectsRequest().type("document").relation("viewer").user("user:anne");

        CompletableFuture<Void> future = streamingApi.streamedListObjects("store123", request, response -> {
            receivedObjects.add(response.getObject());
            latch.countDown();
        });

        // Assert: Should handle large stream efficiently
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Should receive all " + objectCount + " objects");
        future.join();

        assertEquals(objectCount, receivedObjects.size());
        assertEquals("document:0", receivedObjects.get(0));
        assertEquals("document:999", receivedObjects.get(objectCount - 1));
    }

    @Test
    void testGenericStreamResult_deserialization() throws Exception {
        // Test that StreamResult<T> properly deserializes for different types
        ObjectMapper mapper = new ObjectMapper();

        // Test with result
        String jsonWithResult = "{\"result\":{\"object\":\"document:1\"}}";
        var resultType = mapper.getTypeFactory()
                .constructParametricType(
                        dev.openfga.sdk.api.model.StreamResult.class, StreamedListObjectsResponse.class);

        Object streamResult = mapper.readValue(jsonWithResult, resultType);
        assertNotNull(streamResult);

        // Test with error - code should be an integer
        String jsonWithError = "{\"error\":{\"code\":400,\"message\":\"Error occurred\"}}";
        Object streamResultWithError = mapper.readValue(jsonWithError, resultType);
        assertNotNull(streamResultWithError);
    }
}
