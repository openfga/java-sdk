package dev.openfga.sdk.api.client;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Executes HTTP requests to OpenFGA API endpoints using the SDK's internal HTTP client.
 * Requests automatically include authentication, retry logic, error handling, and configuration settings.
 *
 * <p>Example:</p>
 * <pre>{@code
 * ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/endpoint")
 *     .pathParam("store_id", storeId)
 *     .body(requestData)
 *     .build();
 *
 * // Typed response
 * ApiResponse<ResponseType> response = client.apiExecutor().send(request, ResponseType.class).get();
 *
 * // Raw JSON
 * ApiResponse<String> response = client.apiExecutor().send(request).get();
 * }</pre>
 */
public class ApiExecutor {
    private final ApiClient apiClient;
    private final Configuration configuration;

    /**
     * Constructs an ApiExecutor instance. Typically called via {@link OpenFgaClient#apiExecutor()}.
     *
     * @param apiClient API client for HTTP operations
     * @param configuration Client configuration
     */
    public ApiExecutor(ApiClient apiClient, Configuration configuration) {
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
    public CompletableFuture<ApiResponse<String>> send(ApiExecutorRequestBuilder requestBuilder)
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
    public <T> CompletableFuture<ApiResponse<T>> send(ApiExecutorRequestBuilder requestBuilder, Class<T> responseType)
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

            String methodName = "apiExecutor:" + requestBuilder.getMethod() + ":" + requestBuilder.getPath();

            return new HttpRequestAttempt<>(httpRequest, methodName, responseType, apiClient, configuration)
                    .attemptHttpRequest();

        } catch (IOException e) {
            return CompletableFuture.failedFuture(new ApiException(e));
        }
    }

    private String buildCompletePath(ApiExecutorRequestBuilder requestBuilder) {
        StringBuilder pathBuilder = new StringBuilder(requestBuilder.getPath());
        Map<String, String> pathParams = requestBuilder.getPathParams();

        // Automatic {store_id} replacement if not provided
        if (pathBuilder.indexOf("{store_id}") != -1 && !pathParams.containsKey("store_id")) {
            if (configuration instanceof dev.openfga.sdk.api.configuration.ClientConfiguration) {
                String storeId = ((dev.openfga.sdk.api.configuration.ClientConfiguration) configuration).getStoreId();
                if (storeId != null) {
                    replaceInBuilder(pathBuilder, "{store_id}", dev.openfga.sdk.util.StringUtil.urlEncode(storeId));
                }
            }
        }

        // Replace path parameters
        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String encodedValue = dev.openfga.sdk.util.StringUtil.urlEncode(entry.getValue());
            replaceInBuilder(pathBuilder, placeholder, encodedValue);
        }

        // Add query parameters (sorted for deterministic order)
        Map<String, String> queryParams = requestBuilder.getQueryParams();
        if (!queryParams.isEmpty()) {
            String queryString = queryParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> dev.openfga.sdk.util.StringUtil.urlEncode(entry.getKey()) + "="
                            + dev.openfga.sdk.util.StringUtil.urlEncode(entry.getValue()))
                    .collect(java.util.stream.Collectors.joining("&"));
            pathBuilder.append(pathBuilder.indexOf("?") != -1 ? "&" : "?").append(queryString);
        }

        return pathBuilder.toString();
    }

    private void replaceInBuilder(StringBuilder builder, String target, String replacement) {
        int index = builder.indexOf(target);
        while (index != -1) {
            builder.replace(index, index + target.length(), replacement);
            index = builder.indexOf(target, index + replacement.length());
        }
    }

    private HttpRequest buildHttpRequest(ApiExecutorRequestBuilder requestBuilder, String path)
            throws FgaInvalidParameterException, IOException {

        HttpRequest.Builder httpRequestBuilder;

        // Build request with or without body
        if (requestBuilder.hasBody()) {
            Object body = requestBuilder.getBody();
            byte[] bodyBytes;

            // Handle String body separately
            if (body instanceof String) {
                bodyBytes = ((String) body).getBytes(java.nio.charset.StandardCharsets.UTF_8);
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
