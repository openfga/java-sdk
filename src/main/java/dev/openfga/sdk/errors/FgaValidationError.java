package dev.openfga.sdk.errors;

public class FgaValidationError extends Exception {
    private final String field;

    public FgaValidationError(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
