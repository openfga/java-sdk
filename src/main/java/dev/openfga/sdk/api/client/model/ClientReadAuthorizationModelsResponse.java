package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.ReadAuthorizationModelsResponse;
import java.util.List;
import java.util.Map;

public class ClientReadAuthorizationModelsResponse extends ReadAuthorizationModelsResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientReadAuthorizationModelsResponse(ApiResponse<ReadAuthorizationModelsResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        ReadAuthorizationModelsResponse response = apiResponse.getData();
        this.setAuthorizationModels(response.getAuthorizationModels());
        this.setContinuationToken(response.getContinuationToken());
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
