package dev.openfga.sdk.errors;

public class FgaInvalidParameterException extends Exception {
    public FgaInvalidParameterException(String message) {
        super(message);
    }

    public FgaInvalidParameterException(String paramName, String functionName) {
        super(message(paramName, functionName));
    }

    public FgaInvalidParameterException(String paramName, String functionName, Throwable cause) {
        super(message(paramName, functionName), cause);
    }

    private static String message(String paramName, String functionName) {
        return String.format("Required parameter %s was invalid when calling %s.", paramName, functionName);
    }
}
