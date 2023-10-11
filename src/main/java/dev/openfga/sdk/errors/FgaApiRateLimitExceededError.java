package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiRateLimitExceededError extends FgaError {
    public FgaApiRateLimitExceededError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaApiRateLimitExceededError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }
}
