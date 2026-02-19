package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.ListObjectsRequest;
import dev.openfga.sdk.api.model.StreamResult;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import dev.openfga.sdk.constants.FgaConstants;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Tests for {@link StreamingApiExecutor}. */
public class StreamingApiExecutorTest {

    private static final String DEFAULT_STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";
    private static final String DEFAULT_AUTH_MODEL_ID = "01G5JAVJ41T49E9TT3SKVS7X1J";
    private static final String DEFAULT_USER = "user:81684243-9356-4421-8fbf-a4f8d36aa31b";
    private static final String DEFAULT_RELATION = "owner";
    private static final String DEFAULT_TYPE = "document";

    private OpenFgaClient fga;
    private HttpClient mockHttpClient;
    private ApiClient mockApiClient;

    @BeforeEach
    public void beforeEachTest() throws Exception {
        mockHttpClient = mock(HttpClient.class);
        var mockHttpClientBuilder = mock(HttpClient.Builder.class);
        when(mockHttpClientBuilder.executor(any())).thenReturn(mockHttpClientBuilder);
        when(mockHttpClientBuilder.build()).thenReturn(mockHttpClient);

        ClientConfiguration clientConfiguration = new ClientConfiguration()
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

    // -----------------------------------------------------------------------
    // Happy-path tests
    // -----------------------------------------------------------------------

    @Test
    public void stream_successfulStream_deliversAllObjects() throws Exception {
        // Given — three streamed objects
        Stream<String> lines = Stream.of(
                "{\"result\":{\"object\":\"document:1\"}}",
                "{\"result\":{\"object\":\"document:2\"}}",
                "{\"result\":{\"object\":\"document:3\"}}");

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();

        ApiExecutorRequestBuilder request = buildStreamedListObjectsRequest();

        // When
        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(request, received::add)
                .get();

        // Then
        assertEquals(3, received.size());
        assertEquals("document:1", received.get(0).getObject());
        assertEquals("document:2", received.get(1).getObject());
        assertEquals("document:3", received.get(2).getObject());
        verify(mockHttpClient, times(1)).sendAsync(any(), any());
    }

    @Test
    public void stream_emptyStream_completesWithNoObjects() throws Exception {
        // Given
        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, Stream.empty());
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();

        // When
        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(
                        buildStreamedListObjectsRequest(), received::add)
                .get();

        // Then
        assertEquals(0, received.size());
    }

    @Test
    public void stream_largeStream_deliversAllObjects() throws Exception {
        // Given — 1 000 objects
        int count = 1_000;
        List<String> jsonLines = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            jsonLines.add("{\"result\":{\"object\":\"document:" + i + "\"}}");
        }

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, jsonLines.stream());
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        AtomicInteger received = new AtomicInteger(0);

        // When
        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(
                        buildStreamedListObjectsRequest(), obj -> received.incrementAndGet())
                .get();

        // Then
        assertEquals(count, received.get());
    }

    // -----------------------------------------------------------------------
    // Error handling tests
    // -----------------------------------------------------------------------

    @Test
    public void stream_errorInStream_callsErrorConsumerAndContinues() throws Exception {
        // Given — one valid result, one stream-level error, another valid result
        Stream<String> lines = Stream.of(
                "{\"result\":{\"object\":\"document:1\"}}",
                "{\"error\":{\"message\":\"Something went wrong\"}}",
                "{\"result\":{\"object\":\"document:2\"}}");

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();

        // When
        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(
                        buildStreamedListObjectsRequest(), received::add, errors::add)
                .get();

        // Then
        assertEquals(2, received.size());
        assertEquals("document:1", received.get(0).getObject());
        assertEquals("document:2", received.get(1).getObject());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getMessage().contains("Something went wrong"));
    }

    @Test
    public void stream_httpError_failsFutureAndCallsErrorConsumer() throws Exception {
        // Given — HTTP 400
        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(400, Stream.empty());
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();

        // When
        CompletableFuture<Void> future = fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(
                buildStreamedListObjectsRequest(), received::add, errors::add);

        // Then — future should complete exceptionally
        assertThrows(Exception.class, future::get);
        assertEquals(0, received.size());
        assertEquals(1, errors.size());
        assertTrue(
                errors.get(0) instanceof dev.openfga.sdk.errors.ApiException,
                "Expected ApiException, got: " + errors.get(0).getClass().getName());
        assertEquals(400, ((dev.openfga.sdk.errors.ApiException) errors.get(0)).getStatusCode());
    }

    @Test
    public void stream_httpError_withNoErrorConsumer_failsFuture() throws Exception {
        // Given
        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(500, Stream.empty());
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // When
        CompletableFuture<Void> future = fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(
                buildStreamedListObjectsRequest(), obj -> {});

        // Then
        assertThrows(Exception.class, future::get);
    }

    // -----------------------------------------------------------------------
    // Request-builder feature parity with ApiExecutor
    // -----------------------------------------------------------------------

    @Test
    public void stream_autoInjectsStoreIdFromConfiguration() throws Exception {
        Stream<String> lines = Stream.of("{\"result\":{\"object\":\"document:1\"}}");

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                .body(defaultListObjectsBody())
                .build();

        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(request, received::add)
                .get();

        assertEquals(1, received.size());

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient, times(1)).sendAsync(captor.capture(), any());
        String uri = captor.getValue().uri().toString();
        assertTrue(uri.contains(DEFAULT_STORE_ID), "URI should contain the configured storeId");
        assertFalse(uri.contains("{store_id}"), "URI should not contain unresolved {store_id} placeholder");
    }

    @Test
    public void stream_customHeadersArePassed() throws Exception {
        Stream<String> lines = Stream.of("{\"result\":{\"object\":\"document:1\"}}");

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                .header("X-Custom-Header", "custom-value")
                .header("X-Request-ID", "test-request-123")
                .body(defaultListObjectsBody())
                .build();

        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(request, received::add)
                .get();

        assertEquals(1, received.size());

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient, times(1)).sendAsync(captor.capture(), any());
        HttpRequest captured = captor.getValue();
        assertEquals(
                "custom-value", captured.headers().firstValue("X-Custom-Header").orElse(null));
        assertEquals(
                "test-request-123",
                captured.headers().firstValue("X-Request-ID").orElse(null));
    }

    @Test
    public void stream_queryParametersAreAppended() throws Exception {
        Stream<String> lines = Stream.of("{\"result\":{\"object\":\"document:1\"}}");

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                .queryParam("consistency", "HIGHER_CONSISTENCY")
                .body(defaultListObjectsBody())
                .build();

        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(request, obj -> {})
                .get();

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient, times(1)).sendAsync(captor.capture(), any());
        String uri = captor.getValue().uri().toString();
        assertTrue(uri.contains("consistency=HIGHER_CONSISTENCY"), "URI should contain the query parameter");
    }

    // -----------------------------------------------------------------------
    // Null / illegal argument guard tests
    // -----------------------------------------------------------------------

    @Test
    public void streamingApiExecutor_throwsForNullResponseType() {
        assertThrows(IllegalArgumentException.class, () -> fga.streamingApiExecutor((Class<Object>) null));
    }

    @Test
    public void streamingApiExecutor_typeReferenceOverload_works() throws Exception {
        TypeReference<StreamResult<StreamedListObjectsResponse>> typeRef =
                new TypeReference<StreamResult<StreamedListObjectsResponse>>() {};

        Stream<String> lines = Stream.of("{\"result\":{\"object\":\"document:1\"}}");
        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        List<StreamedListObjectsResponse> received = new ArrayList<>();
        fga.streamingApiExecutor(typeRef).stream(buildStreamedListObjectsRequest(), received::add)
                .get();

        assertEquals(1, received.size());
        assertEquals("document:1", received.get(0).getObject());
    }

    @Test
    public void stream_throwsForNullRequestBuilder() {
        StreamingApiExecutor<StreamedListObjectsResponse> executor =
                fga.streamingApiExecutor(StreamedListObjectsResponse.class);
        assertThrows(IllegalArgumentException.class, () -> executor.stream(null, obj -> {}));
    }

    @Test
    public void stream_throwsForNullConsumer() {
        StreamingApiExecutor<StreamedListObjectsResponse> executor =
                fga.streamingApiExecutor(StreamedListObjectsResponse.class);
        ApiExecutorRequestBuilder request = buildStreamedListObjectsRequest();
        assertThrows(IllegalArgumentException.class, () -> executor.stream(request, null));
    }

    @Test
    public void stream_storeIdRequired() throws Exception {
        ClientConfiguration cfg = new ClientConfiguration()
                .apiUrl(FgaConstants.TEST_API_URL)
                .credentials(new Credentials())
                .readTimeout(Duration.ofMillis(250));

        OpenFgaClient clientWithoutStore = new OpenFgaClient(cfg, mockApiClient);
        StreamingApiExecutor<StreamedListObjectsResponse> executor =
                clientWithoutStore.streamingApiExecutor(StreamedListObjectsResponse.class);

        CompletableFuture<Void> future = executor.stream(buildStreamedListObjectsRequest(), obj -> {});
        assertThrows(ExecutionException.class, future::get);
    }

    // -----------------------------------------------------------------------
    // Chaining & CompletableFuture semantics
    // -----------------------------------------------------------------------

    @Test
    public void stream_supportsChaining() throws Exception {
        Stream<String> lines =
                Stream.of("{\"result\":{\"object\":\"document:1\"}}", "{\"result\":{\"object\":\"document:2\"}}");

        HttpResponse<Stream<String>> mockResponse = mockStreamResponse(200, lines);
        when(mockHttpClient.<Stream<String>>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        AtomicInteger completionFlag = new AtomicInteger(0);
        List<StreamedListObjectsResponse> received = new ArrayList<>();

        fga.streamingApiExecutor(StreamedListObjectsResponse.class).stream(
                        buildStreamedListObjectsRequest(), received::add)
                .thenRun(() -> completionFlag.set(1))
                .thenRun(() -> completionFlag.set(2))
                .get();

        assertEquals(2, received.size());
        assertEquals(2, completionFlag.get());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private ApiExecutorRequestBuilder buildStreamedListObjectsRequest() {
        return ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                .body(defaultListObjectsBody())
                .build();
    }

    private ListObjectsRequest defaultListObjectsBody() {
        return new ListObjectsRequest()
                .type(DEFAULT_TYPE)
                .relation(DEFAULT_RELATION)
                .user(DEFAULT_USER);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<Stream<String>> mockStreamResponse(int statusCode, Stream<String> body) {
        HttpResponse<Stream<String>> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn(body);

        HttpHeaders mockHeaders = mock(HttpHeaders.class);
        when(mockHeaders.map()).thenReturn(Map.of("content-type", List.of("application/json")));
        when(mockResponse.headers()).thenReturn(mockHeaders);

        return mockResponse;
    }
}
