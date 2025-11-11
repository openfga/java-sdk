package dev.openfga.sdk.errors;

import static dev.openfga.sdk.errors.HttpStatusCode.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import dev.openfga.sdk.constants.FgaConstants;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FgaError extends ApiException {
    /**
     * Shared ObjectMapper instance for parsing error responses.
     * ObjectMapper is thread-safe for read operations (parsing JSON).
     * This instance is shared across all error classes to reduce memory overhead.
     */
    private static final ObjectMapper ERROR_MAPPER = new ObjectMapper();

    private String method = null;
    private String requestUrl = null;
    private String clientId = null;
    private String audience = null;
    private String grantType = null;
    private String requestId = null;
    private String apiErrorCode = null;
    private String apiErrorMessage = null;
    private String operationName = null;
    private String retryAfterHeader = null;

    /**
     * Metadata map for additional error context.
     * <p>
     * Note: Error instances follow a single-threaded lifecycle (create → populate → throw → catch).
     * They are not shared between threads, so thread-safety is not required.
     */
    private final Map<String, Object> metadata = new HashMap<>();

    public FgaError(String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }

    /**
     * Container for parsed error response data.
     */
    private static class ParsedErrorResponse {
        final String message;
        final String code;
        final JsonNode rootNode;

        ParsedErrorResponse(String message, String code, JsonNode rootNode) {
            this.message = message;
            this.code = code;
            this.rootNode = rootNode;
        }
    }

    /**
     * Parse the API error response body once to extract the error message, code, and root JSON node.
     * This method parses the JSON only once and extracts all needed fields, improving efficiency.
     *
     * @param methodName The API method name that was called
     * @param responseBody The response body JSON string
     * @return ParsedErrorResponse containing message, code, and root JSON node
     */
    private static ParsedErrorResponse parseErrorResponse(String methodName, String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new ParsedErrorResponse(methodName, null, null);
        }

        try {
            JsonNode rootNode = ERROR_MAPPER.readTree(responseBody);

            // Extract message field
            JsonNode messageNode = rootNode.get("message");
            String message = (messageNode != null && !messageNode.isNull()) ? messageNode.asText() : null;

            // Extract code field
            JsonNode codeNode = rootNode.get("code");
            String code = (codeNode != null && !codeNode.isNull()) ? codeNode.asText() : null;

            // If we have a message, use it, otherwise fall back to method name
            String finalMessage = (message != null && !message.trim().isEmpty()) ? message : methodName;

            return new ParsedErrorResponse(finalMessage, code, rootNode);
        } catch (Exception e) {
            // If parsing fails, fall back to the method name
            // This is intentional to ensure errors are still reported even if the response format is unexpected
            return new ParsedErrorResponse(methodName, null, null);
        }
    }

    public static Optional<FgaError> getError(
            String name,
            HttpRequest request,
            Configuration configuration,
            HttpResponse<String> response,
            Throwable previousError) {
        int status = response.statusCode();

        // FGA and OAuth2 servers are only expected to return HTTP 2xx responses.
        if (isSuccessful(status)) {
            return Optional.empty();
        }

        final String body = response.body();
        final var headers = response.headers();

        // Parse the error response once to extract message, code, and JSON node
        final ParsedErrorResponse parsedResponse = parseErrorResponse(name, body);
        final FgaError error;

        if (status == BAD_REQUEST || status == UNPROCESSABLE_ENTITY) {
            error = new FgaApiValidationError(
                    parsedResponse.message, previousError, status, headers, body, parsedResponse.rootNode);
        } else if (status == UNAUTHORIZED || status == FORBIDDEN) {
            error = new FgaApiAuthenticationError(parsedResponse.message, previousError, status, headers, body);
        } else if (status == NOT_FOUND) {
            error = new FgaApiNotFoundError(parsedResponse.message, previousError, status, headers, body);
        } else if (status == TOO_MANY_REQUESTS) {
            error = new FgaApiRateLimitExceededError(parsedResponse.message, previousError, status, headers, body);
        } else if (isServerError(status)) {
            error = new FgaApiInternalError(parsedResponse.message, previousError, status, headers, body);
        } else {
            error = new FgaError(parsedResponse.message, previousError, status, headers, body);
        }

        error.setMethod(request.method());
        error.setRequestUrl(configuration.getApiUrl());

        // Set the operation name
        error.setOperationName(name);

        // Set API error code if extracted from response
        if (parsedResponse.code != null) {
            error.setApiErrorCode(parsedResponse.code);
        }

        // Set the API error message (same as what was parsed for the constructor)
        // This allows getMessage() to return a formatted version
        if (!parsedResponse.message.equals(name)) {
            // Only set apiErrorMessage if we actually got a message from the API
            // (not just falling back to the operation name)
            error.setApiErrorMessage(parsedResponse.message);
        }

        // Extract and set request ID from response headers if present
        // Common request ID header names
        Optional<String> requestId = headers.firstValue("X-Request-Id")
                .or(() -> headers.firstValue("x-request-id"))
                .or(() -> headers.firstValue("Request-Id"));
        if (requestId.isPresent()) {
            error.setRequestId(requestId.get());
        }

        // Extract and set Retry-After header if present
        Optional<String> retryAfter = headers.firstValue(FgaConstants.RETRY_AFTER_HEADER_NAME);
        if (retryAfter.isPresent()) {
            error.setRetryAfterHeader(retryAfter.get());
        }

        var credentials = configuration.getCredentials();
        if (CredentialsMethod.CLIENT_CREDENTIALS == credentials.getCredentialsMethod()) {
            var clientCredentials = credentials.getClientCredentials();
            error.setClientId(clientCredentials.getClientId());
            error.setAudience(clientCredentials.getApiAudience());
        }

        // Unknown error
        return Optional.of(error);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the HTTP method used for the request that caused this error.
     *
     * @return The HTTP method (e.g., "GET", "POST"), or null if not set
     */
    public String getMethod() {
        return method;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * Gets the API URL for the request that caused this error.
     *
     * @return The request URL, or null if not set
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the OAuth2 client ID used in the request, if client credentials authentication was used.
     *
     * @return The client ID, or null if not using client credentials or not set
     */
    public String getClientId() {
        return clientId;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    /**
     * Gets the OAuth2 audience used in the request, if client credentials authentication was used.
     *
     * @return The audience, or null if not using client credentials or not set
     */
    public String getAudience() {
        return audience;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    /**
     * Gets the OAuth2 grant type used in the request.
     *
     * @return The grant type, or null if not set
     */
    public String getGrantType() {
        return grantType;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the request ID from the response headers, useful for debugging and support.
     *
     * @return The request ID (from X-Request-Id header), or null if not present
     */
    public String getRequestId() {
        return requestId;
    }

    public void setApiErrorCode(String apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }

    /**
     * Gets the error code returned by the API in the response body.
     *
     * @return The API error code, or null if not available in the response
     */
    public String getApiErrorCode() {
        return apiErrorCode;
    }

    /**
     * Get the API error code.
     * This is an alias for getApiErrorCode() for convenience.
     * @return The API error code from the response
     */
    public String getCode() {
        return apiErrorCode;
    }

    public void setRetryAfterHeader(String retryAfterHeader) {
        this.retryAfterHeader = retryAfterHeader;
    }

    /**
     * Gets the Retry-After header value from rate limit responses.
     *
     * @return The Retry-After header value (in seconds or HTTP date), or null if not present
     */
    public String getRetryAfterHeader() {
        return retryAfterHeader;
    }

    public void setApiErrorMessage(String apiErrorMessage) {
        this.apiErrorMessage = apiErrorMessage;
    }

    /**
     * Gets the error message parsed from the API response body.
     *
     * @return The API error message, or null if not available in the response
     */
    public String getApiErrorMessage() {
        return apiErrorMessage;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Gets the operation name that resulted in this error.
     *
     * @return The operation name (e.g., "check", "write"), or null if not set
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Gets the metadata map containing additional error context.
     *
     * @return A map of metadata key-value pairs (never null)
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void addMetadata(String key, Object value) {
        getMetadata().put(key, value);
    }

    /**
     * Provides access to the shared ObjectMapper for subclasses.
     * This mapper is thread-safe for read operations.
     *
     * @return The shared ObjectMapper instance
     */
    protected static ObjectMapper getErrorMapper() {
        return ERROR_MAPPER;
    }

    /**
     * Override getMessage() to return the actual API error message
     * instead of the generic operation name.
     *
     * This makes errors understandable everywhere they're displayed:
     * - Exception stack traces
     * - IDE error tooltips
     * - Log files
     * - toString() output
     */
    @Override
    public String getMessage() {
        // Return the actual API error if available
        if (apiErrorMessage != null && !apiErrorMessage.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            // Include operation context
            if (operationName != null) {
                sb.append("[").append(operationName).append("] ");
            }

            // Main error message from API
            sb.append(apiErrorMessage);

            // Add error code if available
            if (apiErrorCode != null) {
                sb.append(" (").append(apiErrorCode).append(")");
            }

            return sb.toString();
        }

        // Fallback to original message (operation name)
        return super.getMessage();
    }

    /**
     * Returns a developer-friendly error message with all context
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();

        if (operationName != null) {
            sb.append("[").append(operationName).append("] ");
        }

        if (apiErrorMessage != null) {
            sb.append(apiErrorMessage);
        } else if (super.getMessage() != null) {
            sb.append(super.getMessage());
        }

        if (apiErrorCode != null) {
            sb.append(" (code: ").append(apiErrorCode).append(")");
        }

        if (requestId != null) {
            sb.append(" [request-id: ").append(requestId).append("]");
        }

        if (getStatusCode() > 0) {
            sb.append(" [HTTP ").append(getStatusCode()).append("]");
        }

        return sb.toString();
    }

    /**
     * Override toString() to provide a formatted string for better logging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());

        if (operationName != null) {
            sb.append(" [").append(operationName).append("]");
        }

        sb.append(": ");

        if (apiErrorMessage != null) {
            sb.append(apiErrorMessage);
        } else if (super.getMessage() != null) {
            sb.append(super.getMessage());
        }

        if (getStatusCode() > 0) {
            sb.append(" (HTTP ").append(getStatusCode()).append(")");
        }

        if (apiErrorCode != null) {
            sb.append(" [code: ").append(apiErrorCode).append("]");
        }

        if (requestId != null) {
            sb.append(" [request-id: ").append(requestId).append("]");
        }

        return sb.toString();
    }
}
