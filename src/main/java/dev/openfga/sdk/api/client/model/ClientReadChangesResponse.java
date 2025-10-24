package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.ReadChangesResponse;
import java.util.List;
import java.util.Map;

public class ClientReadChangesResponse extends ReadChangesResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientReadChangesResponse(ApiResponse<ReadChangesResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        ReadChangesResponse response = apiResponse.getData();
        this.setChanges(response.getChanges());
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
