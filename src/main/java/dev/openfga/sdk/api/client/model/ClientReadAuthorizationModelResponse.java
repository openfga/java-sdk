package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.ReadAuthorizationModelResponse;
import dev.openfga.sdk.api.model.ReadAuthorizationModelsResponse;
import java.util.List;
import java.util.Map;

public class ClientReadAuthorizationModelResponse extends ReadAuthorizationModelResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientReadAuthorizationModelResponse(ApiResponse<ReadAuthorizationModelResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        ReadAuthorizationModelResponse response = apiResponse.getData();
        this.setAuthorizationModel(response.getAuthorizationModel());
    }

    private ClientReadAuthorizationModelResponse(
            int statusCode, Map<String, List<String>> headers, String rawResponse) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.rawResponse = rawResponse;
    }

    /**
     * Get only the first response from a ReadAuthorizationModelsResponse
     */
    public static ClientReadAuthorizationModelResponse latestOf(
            ApiResponse<ReadAuthorizationModelsResponse> apiResponse) {
        ClientReadAuthorizationModelResponse clientResponse = new ClientReadAuthorizationModelResponse(
                apiResponse.getStatusCode(), apiResponse.getHeaders(), apiResponse.getRawResponse());
        ReadAuthorizationModelsResponse response = apiResponse.getData();
        clientResponse.setAuthorizationModel(response.getAuthorizationModels().get(0));
        return clientResponse;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}
