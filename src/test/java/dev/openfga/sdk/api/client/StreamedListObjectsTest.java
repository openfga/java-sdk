package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientStreamedListObjectsOptions;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for streaming list objects functionality with CompletableFuture. */
public class StreamedListObjectsTest {
    private static final String DEFAULT_STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";
    private static final String DEFAULT_AUTH_MODEL_ID = "01G5JAVJ41T49E9TT3SKVS7X1J";
    private static final String DEFAULT_USER = "user:81684243-9356-4421-8fbf-a4f8d36aa31b";
    private static final String DEFAULT_RELATION = "owner";
    private static final String DEFAULT_TYPE = "document";

    private OpenFgaClient fga;
    private ClientConfiguration clientConfiguration;
    private HttpClient mockHttpClient;
    private ApiClient mockApiClient;

    @BeforeEach
    public void beforeEachTest() throws Exception {
        mockHttpClient = mock(HttpClient.class);
        var mockHttpClientBuilder = mock(HttpClient.Builder.class);
        when(mockHttpClientBuilder.executor(any())).thenReturn(mockHttpClientBuilder);
        when(mockHttpClientBuilder.build()).thenReturn(mockHttpClient);

        clientConfiguration = new ClientConfiguration()
                .storeId(DEFAULT_STORE_ID)
                .authorizationModelId(DEFAULT_AUTH_MODEL_ID)
                .apiUrl(FgaConstants.TEST_API_URL)
                .credentials(new Credentials())
                .readTimeout(Duration.ofMillis(250));

        mockApiClient = mock(ApiClient.class);
        when(mockApiClient.getHttpClient()).thenReturn(mockHttpClient);
        when(mockApiClient.getObjectMapper()).thenReturn(new ObjectMapper());
        when(mockApiClient.getHttpClientBuilder()).thenReturn(mockHttpClientBuilder);

        fga = new OpenFgaClient(clientConfiguration, mockApiClient);
    }

    @Test
    public void streamedListObjects_success() throws Exception {
        // Given
        String line1 = "{\"result\":{\"object\":\"document:1\"}}";
        String line2 = "{\"result\":{\"object\":\"document:2\"}}";
        String line3 = "{\"result\":{\"object\":\"document:3\"}}";
        Stream<String> streamResponse = Stream.of(line1, line2, line3);

        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(200, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        List<String> receivedObjects = new ArrayList<>();
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When
        CompletableFuture<Void> future = fga.streamedListObjects(request, receivedObjects::add);
        future.get(); // Wait for completion

        // Then
        assertEquals(3, receivedObjects.size());
        assertEquals("document:1", receivedObjects.get(0));
        assertEquals("document:2", receivedObjects.get(1));
        assertEquals("document:3", receivedObjects.get(2));
        verify(mockHttpClient, times(1)).sendAsync(any(), any());
    }

    @Test
    public void streamedListObjects_withOptions() throws Exception {
        // Given
        String line1 = "{\"result\":{\"object\":\"document:1\"}}";
        Stream<String> streamResponse = Stream.of(line1);

        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(200, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        List<String> receivedObjects = new ArrayList<>();
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        ClientStreamedListObjectsOptions options = new ClientStreamedListObjectsOptions()
                .authorizationModelId("custom-model-id")
                .consistency(ConsistencyPreference.HIGHER_CONSISTENCY);

        // When
        CompletableFuture<Void> future = fga.streamedListObjects(request, options, receivedObjects::add);
        future.get(); // Wait for completion

        // Then
        assertEquals(1, receivedObjects.size());
        assertEquals("document:1", receivedObjects.get(0));
    }

    @Test
    public void streamedListObjects_emptyStream() throws Exception {
        // Given
        Stream<String> streamResponse = Stream.empty();

        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(200, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        List<String> receivedObjects = new ArrayList<>();
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When
        CompletableFuture<Void> future = fga.streamedListObjects(request, receivedObjects::add);
        future.get(); // Wait for completion

        // Then
        assertEquals(0, receivedObjects.size());
    }

    @Test
    public void streamedListObjects_storeIdRequired() {
        // Given
        clientConfiguration.storeId(null);
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When/Then
        var exception = assertThrows(FgaInvalidParameterException.class, () -> {
            fga.streamedListObjects(request, obj -> {});
        });

        assertEquals(
                "Required parameter storeId was invalid when calling ClientConfiguration.", exception.getMessage());
    }

    @Test
    public void streamedListObjects_errorHandling() throws Exception {
        // Given
        String line1 = "{\"result\":{\"object\":\"document:1\"}}";
        String line2 = "{\"error\":{\"message\":\"Something went wrong\"}}";
        String line3 = "{\"result\":{\"object\":\"document:2\"}}";
        Stream<String> streamResponse = Stream.of(line1, line2, line3);

        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(200, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        List<String> receivedObjects = new ArrayList<>();
        List<Throwable> receivedErrors = new ArrayList<>();
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When
        CompletableFuture<Void> future =
                fga.streamedListObjects(request, null, receivedObjects::add, receivedErrors::add);
        future.get(); // Wait for completion

        // Then
        assertEquals(2, receivedObjects.size());
        assertEquals("document:1", receivedObjects.get(0));
        assertEquals("document:2", receivedObjects.get(1));
        assertEquals(1, receivedErrors.size());
    }

    @Test
    public void streamedListObjects_httpError() throws Exception {
        // Given
        Stream<String> streamResponse = Stream.empty();
        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(400, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        List<String> receivedObjects = new ArrayList<>();
        List<Throwable> receivedErrors = new ArrayList<>();
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When
        CompletableFuture<Void> future =
                fga.streamedListObjects(request, null, receivedObjects::add, receivedErrors::add);

        try {
            future.get(); // Wait for completion - should fail
            fail("Expected exception");
        } catch (Exception e) {
            // Expected
        }

        // Then
        assertEquals(0, receivedObjects.size());
        assertEquals(1, receivedErrors.size());
    }

    @Test
    public void streamedListObjects_consumerInvocationCount() throws Exception {
        // Given
        int expectedCount = 100;
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < expectedCount; i++) {
            lines.add(String.format("{\"result\":{\"object\":\"document:%d\"}}", i));
        }
        Stream<String> streamResponse = lines.stream();

        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(200, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        AtomicInteger callCount = new AtomicInteger(0);
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When
        CompletableFuture<Void> future = fga.streamedListObjects(request, obj -> callCount.incrementAndGet());
        future.get(); // Wait for completion

        // Then
        assertEquals(expectedCount, callCount.get());
    }

    @Test
    public void streamedListObjects_chainingWithOtherOperations() throws Exception {
        // Given
        String line1 = "{\"result\":{\"object\":\"document:1\"}}";
        Stream<String> streamResponse = Stream.of(line1);

        HttpResponse<Stream<String>> mockResponse = createMockStreamResponse(200, streamResponse);
        CompletableFuture<HttpResponse<Stream<String>>> responseFuture =
                CompletableFuture.completedFuture(mockResponse);

        when(mockHttpClient.<Stream<String>>sendAsync(any(), any())).thenReturn(responseFuture);

        List<String> receivedObjects = new ArrayList<>();
        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);

        // When - Chain with other async operations
        AtomicInteger completionFlag = new AtomicInteger(0);
        CompletableFuture<Void> future = fga.streamedListObjects(request, receivedObjects::add)
                .thenRun(() -> completionFlag.set(1))
                .thenRun(() -> completionFlag.set(2));

        future.get(); // Wait for all chained operations

        // Then
        assertEquals(1, receivedObjects.size());
        assertEquals(2, completionFlag.get());
    }

    private HttpResponse<Stream<String>> createMockStreamResponse(int statusCode, Stream<String> body) {
        HttpResponse<Stream<String>> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn(body);

        // Create mock headers
        HttpHeaders mockHeaders = mock(HttpHeaders.class);
        when(mockHeaders.map()).thenReturn(Map.of("content-type", List.of("application/json")));
        when(mockResponse.headers()).thenReturn(mockHeaders);

        return mockResponse;
    }
}