package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiAuthenticationError extends FgaError {
    public FgaApiAuthenticationError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaApiAuthenticationError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }
}
