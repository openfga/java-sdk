package dev.openfga.sdk.api.client;

/**
 * Marker class to indicate that the response should not be deserialized by HttpRequestAttempt.
 * Instead, the raw response string should be returned for manual parsing (e.g., NDJSON).
 */
public class StreamingResponseString {
    private final String rawResponse;

    public StreamingResponseString(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}
