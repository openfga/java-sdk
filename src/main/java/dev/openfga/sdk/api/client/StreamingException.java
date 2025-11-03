package dev.openfga.sdk.api.client;

import dev.openfga.sdk.api.model.Status;

public class StreamingException extends RuntimeException {
    private final Status error;

    public StreamingException(Status error) {
        super(formatErrorMessage(error));
        this.error = error;
    }

    public StreamingException(String message, Throwable cause) {
        super(message, cause);
        this.error = null;
    }

    public Status getError() {
        return error;
    }

    public Integer getCode() {
        return error != null ? error.getCode() : null;
    }

    private static String formatErrorMessage(Status error) {
        String codeStr = error.getCode() != null ? String.valueOf(error.getCode()) : "unknown";
        String messageStr = error.getMessage() != null ? error.getMessage() : "Unknown error";
        return String.format("Error in streaming response: code=%s, message=%s", codeStr, messageStr);
    }
}
