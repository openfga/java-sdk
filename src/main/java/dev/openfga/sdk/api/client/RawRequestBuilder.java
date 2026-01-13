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
 * RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/endpoint")
 *     .pathParam("store_id", storeId)
 *     .queryParam("limit", "50")
 *     .body(requestObject);
 * }</pre>
 */
public class RawRequestBuilder {
    private final String method;
    private final String path;
    private final Map<String, String> pathParams;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private Object body;

    private RawRequestBuilder(String method, String path) {
        this.method = method;
        this.path = path;
        this.pathParams = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.body = null;
    }

    /**
     * Creates a new RawRequestBuilder instance.
     *
     * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param path API path with optional placeholders like {store_id}
     * @return New RawRequestBuilder instance
     */
    public static RawRequestBuilder builder(String method, String path) {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP method cannot be null or empty");
        }
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        return new RawRequestBuilder(method.toUpperCase(), path);
    }

    /**
     * Adds a path parameter for placeholder replacement. Values are automatically URL-encoded.
     *
     * @param key Parameter name (without braces)
     * @param value Parameter value
     * @return This builder instance
     */
    public RawRequestBuilder pathParam(String key, String value) {
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
    public RawRequestBuilder queryParam(String key, String value) {
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
    public RawRequestBuilder header(String key, String value) {
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
    public RawRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    String getMethod() {
        return method;
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

