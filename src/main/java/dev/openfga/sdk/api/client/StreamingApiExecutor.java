package dev.openfga.sdk.api.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.BaseStreamingApi;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.model.StreamResult;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Executes HTTP requests to OpenFGA streaming API endpoints using the SDK's internal HTTP client.
 * Requests automatically include authentication, error handling, and configuration settings.
 * Each streamed response object is delivered to a consumer callback asynchronously.
 *
 * <p>This class is analogous to {@link ApiExecutor} but for streaming endpoints that return
 * multiple response objects rather than a single JSON object. It reuses all the
 * streaming infrastructure from {@link BaseStreamingApi}.</p>
 *
 * <p>Obtain an instance via {@link OpenFgaClient#streamingApiExecutor(Class)} — pass the
 * response type directly, no {@link TypeReference} boilerplate required:</p>
 *
 * <pre>{@code
 * ApiExecutorRequestBuilder request =
 *     ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
 *         .body(listObjectsRequest)
 *         .build();
 *
 * client.streamingApiExecutor(StreamedListObjectsResponse.class)
 *     .stream(request, response -> System.out.println(response.getObject()))
 *     .thenRun(() -> System.out.println("Done"));
 * }</pre>
 *
 * <p>If your response type is itself generic, use the {@link TypeReference} overload
 * {@link OpenFgaClient#streamingApiExecutor(TypeReference)} instead.</p>
 */
public class StreamingApiExecutor<T> extends BaseStreamingApi<T> {

    /**
     * Constructs a StreamingApiExecutor from a plain {@link Class}.
     * The SDK builds the required {@code TypeReference<StreamResult<T>>} internally.
     * This is the preferred constructor for concrete (non-generic) response types.
     *
     * @param apiClient     API client for HTTP operations
     * @param configuration Client configuration
     * @param responseType  The class of individual response objects (e.g. {@code StreamedListObjectsResponse.class})
     */
    public StreamingApiExecutor(ApiClient apiClient, Configuration configuration, Class<T> responseType) {
        this(
                apiClient,
                configuration,
                buildTypeReference(
                        requireNonNull(apiClient, "ApiClient cannot be null").getObjectMapper(),
                        requireNonNull(responseType, "Response type cannot be null")));
    }

    /**
     * Constructs a StreamingApiExecutor from an explicit {@link TypeReference}.
     * Use this overload when the response type {@code T} is itself generic.
     * For concrete types, prefer {@link #StreamingApiExecutor(ApiClient, Configuration, Class)}.
     *
     * @param apiClient     API client for HTTP operations
     * @param configuration Client configuration
     * @param typeRef       TypeReference describing {@code StreamResult<T>} for deserialization
     */
    public StreamingApiExecutor(
            ApiClient apiClient, Configuration configuration, TypeReference<StreamResult<T>> typeRef) {
        super(
                requireNonNull(configuration, "Configuration cannot be null"),
                requireNonNull(apiClient, "ApiClient cannot be null"),
                requireNonNull(typeRef, "TypeReference cannot be null"));
    }

    /**
     * Builds a TypeReference<StreamResult<T>> from a plain Class<T> using Jackson's TypeFactory,
     * so the caller never has to mention StreamResult.
     */
    private static <T> TypeReference<StreamResult<T>> buildTypeReference(
            ObjectMapper objectMapper, Class<T> responseType) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(StreamResult.class, responseType);
        return new TypeReference<StreamResult<T>>() {
            @Override
            public JavaType getType() {
                return javaType;
            }
        };
    }

    private static <V> V requireNonNull(V value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Executes a streaming HTTP request. Each received response object is delivered to the consumer
     * callback asynchronously as it arrives.
     *
     * @param requestBuilder Request configuration (path, method, body, headers, etc.)
     * @param consumer Callback invoked for each successfully parsed response object
     * @return CompletableFuture&lt;Void&gt; that completes when the stream is exhausted
     * @throws FgaInvalidParameterException If configuration is invalid
     * @throws ApiException If request construction fails
     */
    public CompletableFuture<Void> stream(ApiExecutorRequestBuilder requestBuilder, Consumer<T> consumer)
            throws FgaInvalidParameterException, ApiException {
        return stream(requestBuilder, consumer, null);
    }

    /**
     * Executes a streaming HTTP request. Each received response object is delivered to the consumer
     * callback asynchronously as it arrives. Errors during streaming are delivered to the optional
     * error consumer.
     *
     * @param requestBuilder Request configuration (path, method, body, headers, etc.)
     * @param consumer Callback invoked for each successfully parsed response object
     * @param errorConsumer Optional callback invoked for errors encountered during streaming
     * @return CompletableFuture&lt;Void&gt; that completes when the stream is exhausted or exceptionally on error
     * @throws FgaInvalidParameterException If configuration is invalid
     * @throws ApiException If request construction fails
     */
    public CompletableFuture<Void> stream(
            ApiExecutorRequestBuilder requestBuilder, Consumer<T> consumer, Consumer<Throwable> errorConsumer)
            throws FgaInvalidParameterException, ApiException {
        if (requestBuilder == null) {
            throw new IllegalArgumentException("Request builder cannot be null");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }

        try {
            configuration.assertValid();
            return processStreamingResponse(
                    requestBuilder.buildHttpRequest(configuration, apiClient), consumer, errorConsumer);
        } catch (IOException e) {
            // JsonProcessingException (body serialisation failure) is the only IOException
            // buildHttpRequest can throw. FgaInvalidParameterException is a separate checked
            // exception declared in the throws clause and propagates directly to the caller.
            ApiException wrapped = new ApiException(e);
            if (errorConsumer != null) {
                errorConsumer.accept(wrapped);
            }
            return CompletableFuture.failedFuture(wrapped);
        }
    }
}
