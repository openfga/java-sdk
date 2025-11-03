package dev.openfga.sdk.api;

import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.client.HttpRequestAttempt;
import dev.openfga.sdk.api.client.StreamingResponseBody;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.model.ListObjectsRequest;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * API handler for streamed list objects operations.
 * This class is separate from the generated OpenFgaApi to avoid modifications to generated code.
 */
public class StreamedListObjectsApi {
    private final Configuration configuration;
    private final ApiClient apiClient;

    public StreamedListObjectsApi(Configuration configuration, ApiClient apiClient) {
        this.configuration = configuration;
        this.apiClient = apiClient;
    }

    /**
     * Stream all objects of the given type that the user has a relation with.
     * Returns raw NDJSON response for parsing by the client layer.
     *
     * @param storeId The store ID (required)
     * @param body The list objects request body (required)
     * @param requestConfiguration The configuration to use for this request
     * @return CompletableFuture with raw streaming response
     * @throws ApiException if fails to make API call
     * @throws FgaInvalidParameterException if required parameters are missing
     */
    public CompletableFuture<ApiResponse<StreamingResponseBody>> streamedListObjects(
            String storeId, ListObjectsRequest body, Configuration requestConfiguration)
            throws ApiException, FgaInvalidParameterException {

        assertParamExists(storeId, "storeId", "streamedListObjects");
        assertParamExists(body, "body", "streamedListObjects");

        String path = "/stores/" + storeId + "/streamed-list-objects";

        try {
            byte[] requestBody = apiClient.getObjectMapper().writeValueAsBytes(body);
            HttpRequest.Builder requestBuilder =
                    ApiClient.requestBuilder("POST", path, requestBody, requestConfiguration);

            HttpRequest httpRequest = requestBuilder.build();

            Map<Attribute, String> telemetryAttributes = new HashMap<>();
            telemetryAttributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, "StreamedListObjects");

            return new HttpRequestAttempt<>(
                            httpRequest,
                            "streamedListObjects",
                            StreamingResponseBody.class,
                            apiClient,
                            requestConfiguration)
                    .addTelemetryAttributes(telemetryAttributes)
                    .attemptHttpRequest();
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new ApiException(e));
        }
    }
}
