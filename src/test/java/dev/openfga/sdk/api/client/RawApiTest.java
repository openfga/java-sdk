package dev.openfga.sdk.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.errors.FgaError;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite for Raw API functionality.
 */
@WireMockTest
public class RawApiTest {
    private static final String DEFAULT_STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";
    private static final String EXPERIMENTAL_ENDPOINT = "/stores/{store_id}/experimental-feature";

    private String fgaApiUrl;

    /**
     * Custom response class for testing typed responses.
     */
    public static class ExperimentalResponse {
        @JsonProperty("success")
        public boolean success;

        @JsonProperty("count")
        public int count;

        @JsonProperty("message")
        public String message;

        public ExperimentalResponse() {}

        public ExperimentalResponse(boolean success, int count, String message) {
            this.success = success;
            this.count = count;
            this.message = message;
        }
    }

    @BeforeEach
    public void beforeEach(WireMockRuntimeInfo wmRuntimeInfo) {
        fgaApiUrl = wmRuntimeInfo.getHttpBaseUrl();
    }

    private OpenFgaClient createClient() throws FgaInvalidParameterException {
        ClientConfiguration config = new ClientConfiguration().apiUrl(fgaApiUrl).storeId(DEFAULT_STORE_ID);
        return new OpenFgaClient(config);
    }

    @Test
    public void rawApi_canAccessViaClient() throws Exception {
        OpenFgaClient client = createClient();
        assertNotNull(client.raw(), "raw() should return a non-null RawApi instance");
    }

    @Test
    public void rawRequestBuilder_canBuildBasicRequest() {
        RawRequestBuilder builder = RawRequestBuilder.builder("GET", "/stores/{store_id}/test");

        assertNotNull(builder);
        assertEquals("GET", builder.getMethod());
        assertEquals("/stores/{store_id}/test", builder.getPath());
    }

    @Test
    public void rawRequestBuilder_canAddPathParameters() {
        RawRequestBuilder builder =
                RawRequestBuilder.builder("GET", "/stores/{store_id}/test").pathParam("store_id", "my-store");

        Map<String, String> pathParams = builder.getPathParams();
        assertEquals(1, pathParams.size());
        assertEquals("my-store", pathParams.get("store_id"));
    }

    @Test
    public void rawRequestBuilder_canAddQueryParameters() {
        RawRequestBuilder builder = RawRequestBuilder.builder("GET", "/test")
                .queryParam("page", "1")
                .queryParam("limit", "10");

        Map<String, String> queryParams = builder.getQueryParams();
        assertEquals(2, queryParams.size());
        assertEquals("1", queryParams.get("page"));
        assertEquals("10", queryParams.get("limit"));
    }

    @Test
    public void rawRequestBuilder_canAddHeaders() {
        RawRequestBuilder builder = RawRequestBuilder.builder("GET", "/test").header("X-Custom-Header", "custom-value");

        Map<String, String> headers = builder.getHeaders();
        assertEquals(1, headers.size());
        assertEquals("custom-value", headers.get("X-Custom-Header"));
    }

    @Test
    public void rawRequestBuilder_canAddBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("key", "value");

        RawRequestBuilder builder = RawRequestBuilder.builder("POST", "/test").body(body);

        assertTrue(builder.hasBody());
        assertEquals(body, builder.getBody());
    }

    @Test
    public void rawRequestBuilder_throwsExceptionForNullMethod() {
        assertThrows(IllegalArgumentException.class, () -> RawRequestBuilder.builder(null, "/test"));
    }

    @Test
    public void rawRequestBuilder_throwsExceptionForEmptyMethod() {
        assertThrows(IllegalArgumentException.class, () -> RawRequestBuilder.builder("", "/test"));
    }

    @Test
    public void rawRequestBuilder_throwsExceptionForNullPath() {
        assertThrows(IllegalArgumentException.class, () -> RawRequestBuilder.builder("GET", null));
    }

    @Test
    public void rawRequestBuilder_throwsExceptionForEmptyPath() {
        assertThrows(IllegalArgumentException.class, () -> RawRequestBuilder.builder("GET", ""));
    }

    @Test
    public void rawApi_canSendGetRequestWithTypedResponse() throws Exception {
        // Setup mock server
        ExperimentalResponse mockResponse = new ExperimentalResponse(true, 42, "Success");
        stubFor(get(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"count\":42,\"message\":\"Success\"}")));

        // Build and send request
        OpenFgaClient client = createClient();
        RawRequestBuilder request =
                RawRequestBuilder.builder("GET", EXPERIMENTAL_ENDPOINT).pathParam("store_id", DEFAULT_STORE_ID);

        ApiResponse<ExperimentalResponse> response =
                client.raw().send(request, ExperimentalResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertTrue(response.getData().success);
        assertEquals(42, response.getData().count);
        assertEquals("Success", response.getData().message);

        // Verify the request was made correctly
        verify(getRequestedFor(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    public void rawApi_canSendPostRequestWithBody() throws Exception {
        // Setup mock server
        stubFor(post(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"count\":1,\"message\":\"Created\"}")));

        // Build and send request
        OpenFgaClient client = createClient();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "test");
        requestBody.put("value", 123);

        RawRequestBuilder request = RawRequestBuilder.builder("POST", EXPERIMENTAL_ENDPOINT)
                .pathParam("store_id", DEFAULT_STORE_ID)
                .body(requestBody);

        ApiResponse<ExperimentalResponse> response =
                client.raw().send(request, ExperimentalResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getData().success);

        // Verify the request was made with the correct body
        verify(postRequestedFor(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.name", equalTo("test")))
                .withRequestBody(matchingJsonPath("$.value", equalTo("123"))));
    }

    @Test
    public void rawApi_canSendRequestWithQueryParameters() throws Exception {
        // Setup mock server
        stubFor(get(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature?force=true&limit=10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"count\":10,\"message\":\"Success\"}")));

        // Build and send request
        OpenFgaClient client = createClient();
        RawRequestBuilder request = RawRequestBuilder.builder("GET", EXPERIMENTAL_ENDPOINT)
                .pathParam("store_id", DEFAULT_STORE_ID)
                .queryParam("force", "true")
                .queryParam("limit", "10");

        ApiResponse<ExperimentalResponse> response =
                client.raw().send(request, ExperimentalResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        // Verify the request was made with query parameters
        verify(getRequestedFor(urlPathEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .withQueryParam("force", equalTo("true"))
                .withQueryParam("limit", equalTo("10")));
    }

    @Test
    public void rawApi_canReturnRawJsonString() throws Exception {
        // Setup mock server
        String jsonResponse = "{\"custom\":\"response\",\"nested\":{\"value\":42}}";
        stubFor(get(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        // Build and send request
        OpenFgaClient client = createClient();
        RawRequestBuilder request =
                RawRequestBuilder.builder("GET", EXPERIMENTAL_ENDPOINT).pathParam("store_id", DEFAULT_STORE_ID);

        ApiResponse<String> response = client.raw().send(request).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals(jsonResponse, response.getData());
        assertEquals(jsonResponse, response.getRawResponse());
    }

    @Test
    public void rawApi_handlesHttpErrors() throws Exception {
        // Setup mock server to return 404
        stubFor(get(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/non-existent"))
                .willReturn(aResponse().withStatus(404).withBody("{\"error\":\"Not found\"}")));

        // Build and send request
        OpenFgaClient client = createClient();
        RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/non-existent")
                .pathParam("store_id", DEFAULT_STORE_ID);

        // Verify exception is thrown
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> client.raw().send(request).get());

        assertTrue(exception.getCause() instanceof FgaError);
    }

    @Test
    public void rawApi_handlesServerErrors() throws Exception {
        // Setup mock server to return 500
        stubFor(get(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .willReturn(aResponse().withStatus(500).withBody("{\"error\":\"Internal server error\"}")));

        // Build and send request
        OpenFgaClient client = createClient();
        RawRequestBuilder request =
                RawRequestBuilder.builder("GET", EXPERIMENTAL_ENDPOINT).pathParam("store_id", DEFAULT_STORE_ID);

        // Verify exception is thrown
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> client.raw().send(request).get());

        assertTrue(exception.getCause() instanceof FgaError);
    }

    @Test
    public void rawApi_supportsCustomHeaders() throws Exception {
        // Setup mock server
        stubFor(get(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"count\":0,\"message\":\"OK\"}")));

        // Build and send request with custom header
        OpenFgaClient client = createClient();
        RawRequestBuilder request = RawRequestBuilder.builder("GET", EXPERIMENTAL_ENDPOINT)
                .pathParam("store_id", DEFAULT_STORE_ID)
                .header("X-Custom-Header", "custom-value")
                .header("X-Request-ID", "12345");

        ApiResponse<ExperimentalResponse> response =
                client.raw().send(request, ExperimentalResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        // Verify custom headers were sent
        verify(getRequestedFor(urlEqualTo("/stores/" + DEFAULT_STORE_ID + "/experimental-feature"))
                .withHeader("X-Custom-Header", equalTo("custom-value"))
                .withHeader("X-Request-ID", equalTo("12345")));
    }

    @Test
    public void rawApi_encodesPathParameters() throws Exception {
        // Setup mock server with encoded path
        String encodedId = "store%20with%20spaces";
        stubFor(get(urlEqualTo("/stores/" + encodedId + "/experimental-feature"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"count\":0,\"message\":\"OK\"}")));

        // Build and send request with special characters
        ClientConfiguration config = new ClientConfiguration().apiUrl(fgaApiUrl).storeId("store with spaces");
        OpenFgaClient client = new OpenFgaClient(config);

        RawRequestBuilder request =
                RawRequestBuilder.builder("GET", EXPERIMENTAL_ENDPOINT).pathParam("store_id", "store with spaces");

        ApiResponse<ExperimentalResponse> response =
                client.raw().send(request, ExperimentalResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        // Verify the path was encoded
        verify(getRequestedFor(urlEqualTo("/stores/" + encodedId + "/experimental-feature")));
    }

    @Test
    public void rawApi_throwsExceptionForNullBuilder() throws Exception {
        OpenFgaClient client = createClient();
        assertThrows(IllegalArgumentException.class, () -> client.raw().send(null));
    }

    @Test
    public void rawApi_throwsExceptionForNullResponseType() throws Exception {
        OpenFgaClient client = createClient();
        RawRequestBuilder request = RawRequestBuilder.builder("GET", "/test");
        assertThrows(IllegalArgumentException.class, () -> client.raw().send(request, null));
    }
}
