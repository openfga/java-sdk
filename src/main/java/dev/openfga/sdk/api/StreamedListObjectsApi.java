package dev.openfga.sdk.api;

import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.client.HttpRequestAttempt;
import dev.openfga.sdk.api.client.StreamingResponseString;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.model.ListObjectsRequest;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * API handler for streamed list objects operations.
 * This class is separate from the generated OpenFgaApi to avoid modifications to generated code.
 */
public class StreamedListObjectsApi {
    private final ApiClient apiClient;

    public StreamedListObjectsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Stream all objects of the given type that the user has a relation with.
     * Returns raw NDJSON response for parsing by the client layer.
     *
     * @param storeId The store ID (required)
     * @param body The list objects request body (required)
     * @param configuration The configuration to use for this request
     * @return CompletableFuture with raw streaming response
     * @throws ApiException if fails to make API call
     * @throws FgaInvalidParameterException if required parameters are missing
     */
    public CompletableFuture<ApiResponse<StreamingResponseString>> streamedListObjects(
            String storeId, ListObjectsRequest body, Configuration configuration)
            throws ApiException, FgaInvalidParameterException {

        assertParamExists(storeId, "storeId", "streamedListObjects");
        assertParamExists(body, "body", "streamedListObjects");

        String path = "/stores/" + storeId + "/streamed-list-objects";

        try {
            // Build the HTTP request
            byte[] requestBody = apiClient.getObjectMapper().writeValueAsBytes(body);
            var bodyPublisher = java.net.http.HttpRequest.BodyPublishers.ofByteArray(requestBody);

            var requestBuilder = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(configuration.getApiUrl() + path))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", configuration.getUserAgent())
                    .POST(bodyPublisher);

            // Add authorization header if needed
            if (configuration.getCredentials() != null
                    && configuration.getCredentials().getApiToken() != null) {
                requestBuilder.header(
                        "Authorization",
                        "Bearer " + configuration.getCredentials().getApiToken());
            }

            // Add default headers
            if (configuration.getDefaultHeaders() != null) {
                configuration.getDefaultHeaders().forEach(requestBuilder::header);
            }

            var httpRequest = requestBuilder.build();

            // Build telemetry attributes
            Map<String, Object> methodParameters = new HashMap<>();
            methodParameters.put("storeId", storeId);
            methodParameters.put("body", body);

            Map<Attribute, String> telemetryAttributes = new HashMap<>();
            telemetryAttributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, "StreamedListObjects");

            // Use HttpRequestAttempt with StreamingResponseString to get raw response
            return new HttpRequestAttempt<>(
                            httpRequest, "streamedListObjects", StreamingResponseString.class, apiClient, configuration)
                    .addTelemetryAttributes(telemetryAttributes)
                    .attemptHttpRequest();
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new ApiException(e));
        }
    }
}
