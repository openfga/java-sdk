package dev.openfga.sdk.errors;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.http.HttpHeaders;

public class FgaApiValidationError extends FgaError {

    // String prefixes for parsing error messages
    private static final String RELATION_PREFIX = "relation '";
    private static final String TYPE_PREFIX = "type '";
    private static final String CHECK_REQUEST_TUPLE_KEY_PREFIX = "CheckRequestTupleKey.";
    private static final String TUPLE_KEY_PREFIX = "TupleKey.";
    private static final String QUOTE_SUFFIX = "'";
    private static final String NOT_FOUND_SUFFIX = "' not found";
    private static final String MUST_NOT_BE_EMPTY = "must not be empty";

    private String invalidField;
    private String invalidValue;

    public FgaApiValidationError(
            String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
        parseValidationDetails(responseBody, null);
    }

    public FgaApiValidationError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
        parseValidationDetails(responseBody, null);
    }

    /**
     * Constructor that accepts a pre-parsed JsonNode to avoid re-parsing the response body.
     * This is more efficient when the JSON has already been parsed by the parent class.
     *
     * @param message The error message
     * @param cause The underlying cause (if any)
     * @param code The HTTP status code
     * @param responseHeaders The response headers
     * @param responseBody The raw response body
     * @param parsedJson The already-parsed JSON root node (may be null)
     */
    public FgaApiValidationError(
            String message,
            Throwable cause,
            int code,
            HttpHeaders responseHeaders,
            String responseBody,
            JsonNode parsedJson) {
        super(message, cause, code, responseHeaders, responseBody);
        parseValidationDetails(responseBody, parsedJson);
    }

    /**
     * Try to extract specific validation details from the error message.
     * <p>
     * This parsing is best-effort and based on current OpenFGA API error message formats.
     * If the message format changes or doesn't match expected patterns, fields will be null.
     * The application should not rely on these fields for critical logic.
     *
     * @param responseBody The API error response body
     * @param parsedJson The already-parsed JSON root node (may be null, in which case we parse it)
     */
    private void parseValidationDetails(String responseBody, JsonNode parsedJson) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return;
        }

        try {
            // Use the pre-parsed JSON node if available, otherwise parse it
            JsonNode root = parsedJson != null ? parsedJson : getErrorMapper().readTree(responseBody);
            String message = root.has("message") ? root.get("message").asText() : null;

            if (message != null) {
                // Parse patterns like: "relation 'document#invalid_relation' not found"
                if (message.contains(RELATION_PREFIX) && message.contains(NOT_FOUND_SUFFIX)) {
                    int start = message.indexOf(RELATION_PREFIX) + RELATION_PREFIX.length();
                    int end = message.indexOf(QUOTE_SUFFIX, start);
                    if (end > start) {
                        this.invalidField = "relation";
                        this.invalidValue = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                        addMetadata("invalid_value", invalidValue);
                    }
                }
                // Parse patterns like: "type 'invalid_type' not found"
                else if (message.contains(TYPE_PREFIX) && message.contains(NOT_FOUND_SUFFIX)) {
                    int start = message.indexOf(TYPE_PREFIX) + TYPE_PREFIX.length();
                    int end = message.indexOf(QUOTE_SUFFIX, start);
                    if (end > start) {
                        this.invalidField = "type";
                        this.invalidValue = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                        addMetadata("invalid_value", invalidValue);
                    }
                }
                // Parse patterns like: "invalid CheckRequestTupleKey.User: value does not match regex..."
                else if (message.contains(CHECK_REQUEST_TUPLE_KEY_PREFIX)) {
                    int start =
                            message.indexOf(CHECK_REQUEST_TUPLE_KEY_PREFIX) + CHECK_REQUEST_TUPLE_KEY_PREFIX.length();
                    // Search for ": " (colon followed by space) for more robust matching
                    int end = message.indexOf(": ", start);
                    if (end > start) {
                        this.invalidField = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                    }
                }
                // Parse patterns like: "invalid TupleKey.User: value does not match regex..."
                else if (message.contains(TUPLE_KEY_PREFIX)) {
                    int start = message.indexOf(TUPLE_KEY_PREFIX) + TUPLE_KEY_PREFIX.length();
                    int end = message.indexOf(": ", start);
                    if (end > start) {
                        this.invalidField = message.substring(start, end);
                        addMetadata("invalid_field", invalidField);
                    }
                }
                // Parse patterns like: "object must not be empty"
                else if (message.contains(MUST_NOT_BE_EMPTY)) {
                    String[] parts = message.trim().split("\\s+");
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

    /**
     * Gets the field name that failed validation, if it could be parsed from the error message.
     *
     * @return The invalid field name (e.g., "relation", "type", "User"), or null if not parsed
     */
    public String getInvalidField() {
        return invalidField;
    }

    /**
     * Gets the invalid value that caused the validation error, if available.
     *
     * @return The invalid value, or null if not parsed from the error message
     */
    public String getInvalidValue() {
        return invalidValue;
    }
}
