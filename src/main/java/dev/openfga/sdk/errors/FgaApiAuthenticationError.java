package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiAuthenticationError extends ApiException {
    public FgaApiAuthenticationError(
            String message, Throwable throwable, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }

    public FgaApiAuthenticationError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        this(message, (Throwable) null, code, responseHeaders, responseBody);
    }
}
