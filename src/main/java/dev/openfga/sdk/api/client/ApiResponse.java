package dev.openfga.sdk.api.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * API response returned by API call.
 *
 * @param <T> The type of data that is deserialized from response body
 */
public class ApiResponse<T> {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;
    private final T data;

    /**
     * @param statusCode The status code of HTTP response
     * @param headers The headers of HTTP response
     * @param data The object deserialized from response bod
     */
    public ApiResponse(int statusCode, Map<String, List<String>> headers, String rawResponse, T data) {
        this.statusCode = statusCode;
        Map<String, List<String>> responseHeaders = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            responseHeaders.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        this.headers = responseHeaders;
        this.rawResponse = rawResponse;
        this.data = data;
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

    public T getData() {
        return data;
    }
}
