package dev.openfga.sdk.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.util.StringUtil;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Terminal step of the fluent builder chain.
     *
     * @return This builder instance
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
        return Collections.unmodifiableMap(pathParams);
    }

    Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    Object getBody() {
        return body;
    }

    boolean hasBody() {
        return body != null;
    }

    /**
     * Resolves path parameters, auto-substitutes {@code {store_id}} from configuration if not
     * explicitly provided, and appends sorted query parameters.
     * Package-private — used by {@link ApiExecutor} and {@link StreamingApiExecutor}.
     */
    String buildPath(Configuration configuration) {
        StringBuilder pathBuilder = new StringBuilder(path);

        // Substitute {store_id} from configuration if not provided explicitly
        if (pathBuilder.indexOf("{store_id}") != -1 && !pathParams.containsKey("store_id")) {
            if (configuration instanceof ClientConfiguration) {
                String storeId = ((ClientConfiguration) configuration).getStoreId();
                if (storeId != null) {
                    replaceAll(pathBuilder, "{store_id}", StringUtil.urlEncode(storeId));
                }
            }
        }

        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            replaceAll(pathBuilder, "{" + entry.getKey() + "}", StringUtil.urlEncode(entry.getValue()));
        }

        // Append query parameters
        if (!queryParams.isEmpty()) {
            String queryString = queryParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> StringUtil.urlEncode(e.getKey()) + "=" + StringUtil.urlEncode(e.getValue()))
                    .collect(Collectors.joining("&"));
            pathBuilder.append(pathBuilder.indexOf("?") != -1 ? "&" : "?").append(queryString);
        }

        return pathBuilder.toString();
    }

    /**
     * Builds a fully configured {@link HttpRequest} from this builder's state.
     * Handles body serialization, custom headers, and request interceptors.
     * Package-private — used by {@link ApiExecutor} and {@link StreamingApiExecutor}.
     */
    HttpRequest buildHttpRequest(Configuration configuration, ApiClient apiClient)
            throws FgaInvalidParameterException, JsonProcessingException {
        String resolvedPath = buildPath(configuration);

        HttpRequest.Builder httpRequestBuilder;
        if (hasBody()) {
            byte[] bodyBytes = body instanceof String
                    ? ((String) body).getBytes(StandardCharsets.UTF_8)
                    : apiClient.getObjectMapper().writeValueAsBytes(body);
            httpRequestBuilder = ApiClient.requestBuilder(method.name(), resolvedPath, bodyBytes, configuration);
        } else {
            httpRequestBuilder = ApiClient.requestBuilder(method.name(), resolvedPath, configuration);
        }

        headers.forEach(httpRequestBuilder::header);

        if (apiClient.getRequestInterceptor() != null) {
            apiClient.getRequestInterceptor().accept(httpRequestBuilder);
        }

        return httpRequestBuilder.build();
    }

    private static void replaceAll(StringBuilder sb, String target, String replacement) {
        int index = sb.indexOf(target);
        while (index != -1) {
            sb.replace(index, index + target.length(), replacement);
            index = sb.indexOf(target, index + replacement.length());
        }
    }
}
