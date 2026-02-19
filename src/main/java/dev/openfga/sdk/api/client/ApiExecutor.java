package dev.openfga.sdk.api.client;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;

/**
 * Executes HTTP requests to OpenFGA API endpoints using the SDK's internal HTTP client.
 * Requests automatically include authentication, retry logic, error handling, and configuration settings.
 *
 * <p>Example:</p>
 * <pre>{@code
 * ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/endpoint")
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

            HttpRequest httpRequest = requestBuilder.buildHttpRequest(configuration, apiClient);
            String methodName = "apiExecutor:" + requestBuilder.getMethod() + ":" + requestBuilder.getPath();

            return new HttpRequestAttempt<>(httpRequest, methodName, responseType, apiClient, configuration)
                    .attemptHttpRequest();

        } catch (IOException e) {
            return CompletableFuture.failedFuture(new ApiException(e));
        }
    }
}
