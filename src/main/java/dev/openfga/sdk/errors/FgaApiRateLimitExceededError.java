package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiRateLimitExceededError extends ApiException {
    public FgaApiRateLimitExceededError(
            String message, Throwable throwable, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }

    public FgaApiRateLimitExceededError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        this(message, (Throwable) null, code, responseHeaders, responseBody);
    }
}
