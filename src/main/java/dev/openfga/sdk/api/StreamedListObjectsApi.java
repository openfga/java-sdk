package dev.openfga.sdk.api;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;
import static dev.openfga.sdk.util.Validation.assertParamExists;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.ConfigurationOverride;
import dev.openfga.sdk.api.model.ListObjectsRequest;
import dev.openfga.sdk.api.model.Status;
import dev.openfga.sdk.api.model.StreamResultOfStreamedListObjectsResponse;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.util.StringUtil;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * API layer for handling streaming responses from the streamedListObjects endpoint.
 * This class provides true asynchronous streaming with consumer callbacks using CompletableFuture
 * and Java 11's HttpClient async streaming capabilities.
 */
public class StreamedListObjectsApi {
    private final Configuration configuration;
    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;

    public StreamedListObjectsApi(Configuration configuration, ApiClient apiClient) {
        this.configuration = configuration;
        this.apiClient = apiClient;
        this.objectMapper = apiClient.getObjectMapper();
    }

    /**
     * Stream all objects of the given type that the user has a relation with.
     * Each streamed response is delivered to the consumer callback asynchronously as it arrives.
     * Returns a CompletableFuture that completes when streaming is finished.
     *
     * @param storeId The store ID
     * @param body The list objects request
     * @param consumer Callback to handle each streamed object response (invoked asynchronously)
     * @return CompletableFuture<Void> that completes when streaming finishes
     * @throws ApiException if the API call fails immediately
     * @throws FgaInvalidParameterException if required parameters are missing
     */
    public CompletableFuture<Void> streamedListObjects(
            String storeId, ListObjectsRequest body, Consumer<String> consumer)
            throws ApiException, FgaInvalidParameterException {
        return streamedListObjects(storeId, body, consumer, null, this.configuration);
    }

    /**
     * Stream all objects of the given type that the user has a relation with.
     * Each streamed response is delivered to the consumer callback asynchronously as it arrives.
     * Returns a CompletableFuture that completes when streaming is finished.
     *
     * @param storeId The store ID
     * @param body The list objects request
     * @param consumer Callback to handle each streamed object response (invoked asynchronously)
     * @param configurationOverride Configuration overrides (e.g., additional headers)
     * @return CompletableFuture<Void> that completes when streaming finishes
     * @throws ApiException if the API call fails immediately
     * @throws FgaInvalidParameterException if required parameters are missing
     */
    public CompletableFuture<Void> streamedListObjects(
            String storeId,
            ListObjectsRequest body,
            Consumer<String> consumer,
            ConfigurationOverride configurationOverride)
            throws ApiException, FgaInvalidParameterException {
        return streamedListObjects(storeId, body, consumer, null, this.configuration.override(configurationOverride));
    }

    /**
     * Stream all objects of the given type that the user has a relation with.
     * Each streamed response is delivered to the consumer callback asynchronously as it arrives.
     * Returns a CompletableFuture that completes when streaming is finished.
     *
     * @param storeId The store ID
     * @param body The list objects request
     * @param consumer Callback to handle each streamed object response (invoked asynchronously)
     * @param errorConsumer Optional callback to handle errors during streaming
     * @return CompletableFuture<Void> that completes when streaming finishes or exceptionally on error
     * @throws ApiException if the API call fails immediately
     * @throws FgaInvalidParameterException if required parameters are missing
     */
    public CompletableFuture<Void> streamedListObjects(
            String storeId, ListObjectsRequest body, Consumer<String> consumer, Consumer<Throwable> errorConsumer)
            throws ApiException, FgaInvalidParameterException {
        return streamedListObjects(storeId, body, consumer, errorConsumer, this.configuration);
    }

    /**
     * Stream all objects of the given type that the user has a relation with.
     * Each streamed response is delivered to the consumer callback asynchronously as it arrives.
     * Returns a CompletableFuture that completes when streaming is finished.
     *
     * @param storeId The store ID
     * @param body The list objects request
     * @param consumer Callback to handle each streamed object response (invoked asynchronously)
     * @param errorConsumer Optional callback to handle errors during streaming
     * @param configurationOverride Configuration overrides (e.g., additional headers)
     * @return CompletableFuture<Void> that completes when streaming finishes or exceptionally on error
     * @throws ApiException if the API call fails immediately
     * @throws FgaInvalidParameterException if required parameters are missing
     */
    public CompletableFuture<Void> streamedListObjects(
            String storeId,
            ListObjectsRequest body,
            Consumer<String> consumer,
            Consumer<Throwable> errorConsumer,
            ConfigurationOverride configurationOverride)
            throws ApiException, FgaInvalidParameterException {
        return streamedListObjects(
                storeId, body, consumer, errorConsumer, this.configuration.override(configurationOverride));
    }

    /**
     * Internal implementation that accepts a final Configuration to use for the request.
     */
    private CompletableFuture<Void> streamedListObjects(
            String storeId,
            ListObjectsRequest body,
            Consumer<String> consumer,
            Consumer<Throwable> errorConsumer,
            Configuration configuration)
            throws ApiException, FgaInvalidParameterException {

        assertParamExists(storeId, "storeId", "streamedListObjects");
        assertParamExists(body, "body", "streamedListObjects");

        String path = "/stores/{store_id}/streamed-list-objects"
                .replace("{store_id}", StringUtil.urlEncode(storeId.toString()));

        try {
            HttpRequest request = buildHttpRequest("POST", path, body, configuration);

            // Use async HTTP client with streaming body handler
            // ofLines() provides line-by-line streaming which is perfect for NDJSON
            return apiClient
                    .getHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                    .thenCompose(response -> {
                        // Check response status
                        int statusCode = response.statusCode();
                        if (statusCode < 200 || statusCode >= 300) {
                            ApiException apiException =
                                    new ApiException(statusCode, "API error: " + statusCode, response.headers(), null);
                            return CompletableFuture.failedFuture(apiException);
                        }

                        // Process the stream - this runs on HttpClient's executor thread
                        try (Stream<String> lines = response.body()) {
                            lines.forEach(line -> {
                                if (!isNullOrWhitespace(line)) {
                                    processLine(line, consumer, errorConsumer);
                                }
                            });
                            return CompletableFuture.completedFuture((Void) null);
                        } catch (Exception e) {
                            return CompletableFuture.failedFuture(e);
                        }
                    })
                    .handle((result, throwable) -> {
                        if (throwable != null) {
                            // Unwrap CompletionException to get the original exception
                            Throwable actualException = throwable;
                            if (throwable instanceof java.util.concurrent.CompletionException
                                    && throwable.getCause() != null) {
                                actualException = throwable.getCause();
                            }

                            if (errorConsumer != null) {
                                errorConsumer.accept(actualException);
                            }
                            // Re-throw to keep the CompletableFuture in failed state
                            if (actualException instanceof RuntimeException) {
                                throw (RuntimeException) actualException;
                            }
                            throw new RuntimeException(actualException);
                        }
                        return result;
                    });

        } catch (Exception e) {
            if (errorConsumer != null) {
                errorConsumer.accept(e);
            }
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Process a single line from the NDJSON stream
     */
    private void processLine(String line, Consumer<String> consumer, Consumer<Throwable> errorConsumer) {
        try {
            // Parse the JSON line to extract the object
            StreamResultOfStreamedListObjectsResponse streamResult =
                    objectMapper.readValue(line, StreamResultOfStreamedListObjectsResponse.class);

            if (streamResult.getError() != null) {
                // Handle error in stream
                if (errorConsumer != null) {
                    Status error = streamResult.getError();
                    String errorMessage = error.getMessage() != null
                            ? "Stream error: " + error.getMessage()
                            : "Stream error: " + (error.getCode() != null ? "code " + error.getCode() : "unknown");
                    errorConsumer.accept(new ApiException(errorMessage));
                }
            } else if (streamResult.getResult() != null) {
                // Deliver the object to the consumer
                StreamedListObjectsResponse result = streamResult.getResult();
                if (result.getObject() != null) {
                    consumer.accept(result.getObject());
                }
            }
        } catch (Exception e) {
            if (errorConsumer != null) {
                errorConsumer.accept(e);
            }
        }
    }

    private HttpRequest buildHttpRequest(String method, String path, Object body, Configuration configuration)
            throws ApiException, FgaInvalidParameterException {
        try {
            byte[] bodyBytes = objectMapper.writeValueAsBytes(body);
            HttpRequest.Builder requestBuilder = ApiClient.requestBuilder(method, path, bodyBytes, configuration);

            // Apply request interceptors if any
            var interceptor = apiClient.getRequestInterceptor();
            if (interceptor != null) {
                interceptor.accept(requestBuilder);
            }

            return requestBuilder.build();
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }
}
