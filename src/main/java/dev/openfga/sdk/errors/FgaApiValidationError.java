package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiValidationError extends FgaError {
    public FgaApiValidationError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaApiValidationError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }
}
