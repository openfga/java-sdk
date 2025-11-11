package dev.openfga.sdk.errors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpHeaders;

public class FgaApiValidationError extends FgaError {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String invalidField;
    private String invalidValue;
    private String expectedFormat;

    public FgaApiValidationError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
        parseValidationDetails(responseBody);
    }

    public FgaApiValidationError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
        parseValidationDetails(responseBody);
    }

    /**
     * Try to extract specific validation details from the error message
     */
    private void parseValidationDetails(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return;
        }

        try {
            JsonNode root = MAPPER.readTree(responseBody);
            String message = root.has("message") ? root.get("message").asText() : null;

            if (message != null) {
                // Parse patterns like: "relation 'document#invalid_relation' not found"
                if (message.contains("relation '") && message.contains("' not found")) {
                    int start = message.indexOf("relation '") + 10;
                    int end = message.indexOf("'", start);
                    if (end > start) {
                        this.invalidField = "relation";
                        this.invalidValue = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                        addMetadata("invalid_value", invalidValue);
                    }
                }
                // Parse patterns like: "type 'invalid_type' not found"
                else if (message.contains("type '") && message.contains("' not found")) {
                    int start = message.indexOf("type '") + 6;
                    int end = message.indexOf("'", start);
                    if (end > start) {
                        this.invalidField = "type";
                        this.invalidValue = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                        addMetadata("invalid_value", invalidValue);
                    }
                }
                // Parse patterns like: "invalid CheckRequestTupleKey.User: value does not match regex..."
                else if (message.contains("invalid CheckRequestTupleKey.")) {
                    int start = message.indexOf("CheckRequestTupleKey.") + 21;
                    int end = message.indexOf(":", start);
                    if (end > start) {
                        this.invalidField = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                    }
                }
                // Parse patterns like: "invalid TupleKey.User: value does not match regex..."
                else if (message.contains("invalid TupleKey.")) {
                    int start = message.indexOf("TupleKey.") + 9;
                    int end = message.indexOf(":", start);
                    if (end > start) {
                        this.invalidField = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                    }
                }
                // Parse patterns like: "object must not be empty"
                else if (message.contains("must not be empty")) {
                    String[] parts = message.split(" ");
                    if (parts.length > 0 && !parts[0].isEmpty()) {
                        this.invalidField = parts[0];
                        addMetadata("invalid_field", invalidField);
                    }
                }
            }
        } catch (Exception e) {
            // Parsing is best-effort, ignore failures
        }
    }

    public String getInvalidField() {
        return invalidField;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getExpectedFormat() {
        return expectedFormat;
    }
}
