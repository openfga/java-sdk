package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiNotFoundError extends FgaError {
    public FgaApiNotFoundError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaApiNotFoundError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }
}
