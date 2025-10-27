package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.ReadResponse;
import java.util.List;
import java.util.Map;

public class ClientReadResponse extends ReadResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientReadResponse(ApiResponse<ReadResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        ReadResponse response = apiResponse.getData();
        this.setTuples(response.getTuples());
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
