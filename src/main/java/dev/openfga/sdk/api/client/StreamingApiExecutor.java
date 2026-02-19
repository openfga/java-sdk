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
 * Executes HTTP requests to OpenFGA streaming endpoints, delivering each response object
 * to a consumer callback as it arrives.
 *
 * <p>Obtain via {@link OpenFgaClient#streamingApiExecutor(Class)}:</p>
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
 * <p>If the response type is itself generic, use {@link OpenFgaClient#streamingApiExecutor(TypeReference)} instead.</p>
 */
public class StreamingApiExecutor<T> extends BaseStreamingApi<T> {

    /**
     * @param apiClient     API client for HTTP operations
     * @param configuration Client configuration
     * @param responseType  Class of the response objects
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
     * Use when the response type {@code T} is itself generic.
     * For concrete types, prefer {@link #StreamingApiExecutor(ApiClient, Configuration, Class)}.
     *
     * @param apiClient     API client for HTTP operations
     * @param configuration Client configuration
     * @param typeRef       TypeReference for {@code StreamResult<T>}
     */
    public StreamingApiExecutor(
            ApiClient apiClient, Configuration configuration, TypeReference<StreamResult<T>> typeRef) {
        super(
                requireNonNull(configuration, "Configuration cannot be null"),
                requireNonNull(apiClient, "ApiClient cannot be null"),
                requireNonNull(typeRef, "TypeReference cannot be null"));
    }

    /** Builds a {@code TypeReference<StreamResult<T>>} from a plain {@code Class<T>}. */
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
     * @param requestBuilder Request configuration
     * @param consumer Callback invoked for each response object
     * @return CompletableFuture&lt;Void&gt; that completes when the stream is exhausted
     * @throws FgaInvalidParameterException If configuration is invalid
     * @throws ApiException If request construction fails
     */
    public CompletableFuture<Void> stream(ApiExecutorRequestBuilder requestBuilder, Consumer<T> consumer)
            throws FgaInvalidParameterException, ApiException {
        return stream(requestBuilder, consumer, null);
    }

    /**
     * @param requestBuilder Request configuration
     * @param consumer Callback invoked for each response object
     * @param errorConsumer Optional callback invoked for stream or HTTP errors
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
