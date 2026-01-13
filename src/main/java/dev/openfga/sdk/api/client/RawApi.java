package dev.openfga.sdk.api.client;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.util.StringUtil;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Executes HTTP requests to OpenFGA API endpoints using the SDK's internal HTTP client.
 * Requests automatically include authentication, retry logic, error handling, and configuration settings.
 *
 * <p>Example:</p>
 * <pre>{@code
 * RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/endpoint")
 *     .pathParam("store_id", storeId)
 *     .body(requestData);
 *
 * // Typed response
 * ApiResponse<ResponseType> response = client.raw().send(request, ResponseType.class).get();
 *
 * // Raw JSON
 * ApiResponse<String> response = client.raw().send(request).get();
 * }</pre>
 */
public class RawApi {
    private final ApiClient apiClient;
    private final Configuration configuration;

    /**
     * Constructs a RawApi instance. Typically called via {@link OpenFgaClient#raw()}.
     *
     * @param apiClient API client for HTTP operations
     * @param configuration Client configuration
     */
    public RawApi(ApiClient apiClient, Configuration configuration) {
        if (apiClient == null) {
            throw new IllegalArgumentException("ApiClient cannot be null");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.apiClient = apiClient;
        this.configuration = configuration;
    }

    /**
     * Executes an HTTP request and returns the response body as a JSON string.
     *
     * @param requestBuilder Request configuration
     * @return CompletableFuture with API response containing string data
     * @throws FgaInvalidParameterException If configuration is invalid
     * @throws ApiException If request construction fails
     */
    public CompletableFuture<ApiResponse<String>> send(RawRequestBuilder requestBuilder)
            throws FgaInvalidParameterException, ApiException {
        return send(requestBuilder, String.class);
    }

    /**
     * Executes an HTTP request and deserializes the response into a typed object.
     *
     * @param <T> Response type
     * @param requestBuilder Request configuration
     * @param responseType Class to deserialize response into
     * @return CompletableFuture with API response containing typed data
     * @throws FgaInvalidParameterException If configuration is invalid
     * @throws ApiException If request construction fails
     */
    public <T> CompletableFuture<ApiResponse<T>> send(RawRequestBuilder requestBuilder, Class<T> responseType)
            throws FgaInvalidParameterException, ApiException {
        if (requestBuilder == null) {
            throw new IllegalArgumentException("Request builder cannot be null");
        }
        if (responseType == null) {
            throw new IllegalArgumentException("Response type cannot be null");
        }

        try {
            configuration.assertValid();

            String completePath = buildCompletePath(requestBuilder);
            HttpRequest httpRequest = buildHttpRequest(requestBuilder, completePath);

            String methodName = "raw:" + requestBuilder.getMethod() + ":" + requestBuilder.getPath();

            return new HttpRequestAttempt<>(httpRequest, methodName, responseType, apiClient, configuration)
                    .attemptHttpRequest();

        } catch (IOException e) {
            return CompletableFuture.failedFuture(new ApiException(e));
        }
    }

    private String buildCompletePath(RawRequestBuilder requestBuilder) {
        String path = requestBuilder.getPath();

        // Replace path parameters
        for (Map.Entry<String, String> entry : requestBuilder.getPathParams().entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String encodedValue = StringUtil.urlEncode(entry.getValue());
            path = path.replace(placeholder, encodedValue);
        }

        // Add query parameters
        Map<String, String> queryParams = requestBuilder.getQueryParams();
        if (!queryParams.isEmpty()) {
            String queryString = queryParams.entrySet().stream()
                    .map(entry -> StringUtil.urlEncode(entry.getKey()) + "=" + StringUtil.urlEncode(entry.getValue()))
                    .collect(Collectors.joining("&"));

            path = path + (path.contains("?") ? "&" : "?") + queryString;
        }

        return path;
    }

    private HttpRequest buildHttpRequest(RawRequestBuilder requestBuilder, String path)
            throws FgaInvalidParameterException, IOException {

        HttpRequest.Builder httpRequestBuilder;

        // Build request with or without body
        if (requestBuilder.hasBody()) {
            Object body = requestBuilder.getBody();
            byte[] bodyBytes;

            // Handle String body separately
            if (body instanceof String) {
                bodyBytes = ((String) body).getBytes();
            } else {
                bodyBytes = apiClient.getObjectMapper().writeValueAsBytes(body);
            }

            httpRequestBuilder = ApiClient.requestBuilder(requestBuilder.getMethod(), path, bodyBytes, configuration);
        } else {
            httpRequestBuilder = ApiClient.requestBuilder(requestBuilder.getMethod(), path, configuration);
        }

        // Add custom headers
        for (Map.Entry<String, String> entry : requestBuilder.getHeaders().entrySet()) {
            httpRequestBuilder.header(entry.getKey(), entry.getValue());
        }

        // Apply request interceptor
        if (apiClient.getRequestInterceptor() != null) {
            apiClient.getRequestInterceptor().accept(httpRequestBuilder);
        }

        return httpRequestBuilder.build();
    }
}

