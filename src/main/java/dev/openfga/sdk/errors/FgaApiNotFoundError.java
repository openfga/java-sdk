package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiNotFoundError extends ApiException {
    public FgaApiNotFoundError(
            String message, Throwable throwable, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }

    public FgaApiNotFoundError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        this(message, (Throwable) null, code, responseHeaders, responseBody);
    }
}
