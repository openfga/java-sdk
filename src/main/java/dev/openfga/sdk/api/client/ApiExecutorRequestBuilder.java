package dev.openfga.sdk.api.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for constructing HTTP requests to OpenFGA API endpoints.
 * Supports path parameter replacement, query parameters, headers, and request bodies.
 * Path and query parameters are automatically URL-encoded.
 *
 * <p>Example:</p>
 * <pre>{@code
 * ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/endpoint")
 *     .pathParam("store_id", storeId)
 *     .queryParam("limit", "50")
 *     .body(requestObject)
 *     .build();
 * }</pre>
 */
public class ApiExecutorRequestBuilder {
    private final HttpMethod method;
    private final String path;
    private final Map<String, String> pathParams;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private Object body;

    private ApiExecutorRequestBuilder(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
        this.pathParams = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.body = null;
    }

    /**
     * Creates a new ApiExecutorRequestBuilder instance.
     *
     * @param method HTTP method enum value (GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS)
     * @param path API path with optional placeholders like {store_id}
     * @return New ApiExecutorRequestBuilder instance
     * @throws IllegalArgumentException if method or path is invalid
     */
    public static ApiExecutorRequestBuilder builder(HttpMethod method, String path) {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method cannot be null");
        }
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        return new ApiExecutorRequestBuilder(method, path);
    }

    /**
     * Adds a path parameter for placeholder replacement. Values are automatically URL-encoded.
     *
     * @param key Parameter name (without braces)
     * @param value Parameter value
     * @return This builder instance
     */
    public ApiExecutorRequestBuilder pathParam(String key, String value) {
        if (key != null && value != null) {
            this.pathParams.put(key, value);
        }
        return this;
    }

    /**
     * Adds a query parameter. Values are automatically URL-encoded.
     *
     * @param key Query parameter name
     * @param value Query parameter value
     * @return This builder instance
     */
    public ApiExecutorRequestBuilder queryParam(String key, String value) {
        if (key != null && value != null) {
            this.queryParams.put(key, value);
        }
        return this;
    }

    /**
     * Adds an HTTP header. Standard headers are managed by the SDK.
     *
     * @param key Header name
     * @param value Header value
     * @return This builder instance
     */
    public ApiExecutorRequestBuilder header(String key, String value) {
        if (key != null && value != null) {
            this.headers.put(key, value);
        }
        return this;
    }

    /**
     * Sets the request body. Objects and Maps are serialized to JSON. Strings are sent as-is.
     *
     * @param body Request body
     * @return This builder instance
     */
    public ApiExecutorRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    /**
     * Builds and returns the request for use with the API Executor.
     * This method must be called to complete request construction.
     *
     * <p>This is required for consistency with other OpenFGA SDKs (e.g., Go SDK)
     * and follows the standard builder pattern.</p>
     *
     * @return This builder instance (ready to be passed to {@link ApiExecutor#send})
     */
    public ApiExecutorRequestBuilder build() {
        return this;
    }

    String getMethod() {
        return method.name();
    }

    String getPath() {
        return path;
    }

    Map<String, String> getPathParams() {
        return new HashMap<>(pathParams);
    }

    Map<String, String> getQueryParams() {
        return new HashMap<>(queryParams);
    }

    Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    Object getBody() {
        return body;
    }

    boolean hasBody() {
        return body != null;
    }
}
