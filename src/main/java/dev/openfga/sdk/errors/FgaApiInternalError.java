package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;

public class FgaApiInternalError extends FgaError {
    public FgaApiInternalError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaApiInternalError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }
}
