package dev.openfga.sdk.errors;

import static dev.openfga.sdk.errors.HttpStatusCode.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import dev.openfga.sdk.constants.FgaConstants;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class FgaError extends ApiException {
    private String method = null;
    private String requestUrl = null;
    private String clientId = null;
    private String audience = null;
    private String grantType = null;
    private String requestId = null;
    private String apiErrorCode = null;
    private String retryAfterHeader = null;
    private String apiErrorMessage = null;
    private String operationName = null;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiErrorResponse {
        @com.fasterxml.jackson.annotation.JsonProperty("code")
        private String code;

        @com.fasterxml.jackson.annotation.JsonProperty("message")
        private String message;

        @com.fasterxml.jackson.annotation.JsonProperty("error")
        private String error;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message != null ? message : error;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public FgaError(String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
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
        final FgaError error;

        if (status == BAD_REQUEST || status == UNPROCESSABLE_ENTITY) {
            error = new FgaApiValidationError(name, previousError, status, headers, body);
        } else if (status == UNAUTHORIZED || status == FORBIDDEN) {
            error = new FgaApiAuthenticationError(name, previousError, status, headers, body);
        } else if (status == NOT_FOUND) {
            error = new FgaApiNotFoundError(name, previousError, status, headers, body);
        } else if (status == TOO_MANY_REQUESTS) {
            error = new FgaApiRateLimitExceededError(name, previousError, status, headers, body);
        } else if (HttpStatusCode.isServerError(status)) {
            error = new FgaApiInternalError(name, previousError, status, headers, body);
        } else {
            error = new FgaError(name, previousError, status, headers, body);
        }

        error.setMethod(request.method());
        error.setRequestUrl(configuration.getApiUrl());

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

        error.setOperationName(name);

        // Parse API error response
        if (body != null && !body.trim().isEmpty()) {
            try {
                ApiErrorResponse resp = OBJECT_MAPPER.readValue(body, ApiErrorResponse.class);
                error.setApiErrorCode(resp.getCode());
                error.setApiErrorMessage(resp.getMessage());
            } catch (JsonProcessingException e) {
                // Fall back, do nothing - log the exception for debugging
                System.err.println("Failed to parse API error response JSON: " + e.getMessage());
            }
        }

        // Extract requestId from headers
        Optional<String> requestIdOpt = headers.firstValue("x-request-id");
        if (requestIdOpt.isPresent()) {
            error.setRequestId(requestIdOpt.get());
        }

        // Unknown error
        return Optional.of(error);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getAudience() {
        return audience;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setApiErrorCode(String apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }

    public String getApiErrorCode() {
        return apiErrorCode;
    }

    public void setRetryAfterHeader(String retryAfterHeader) {
        this.retryAfterHeader = retryAfterHeader;
    }

    public String getRetryAfterHeader() {
        return retryAfterHeader;
    }

    public void setApiErrorMessage(String apiErrorMessage) {
        this.apiErrorMessage = apiErrorMessage;
    }

    public String getApiErrorMessage() {
        return apiErrorMessage;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }

    /**
     * Returns a formatted error message for FgaError.
     * <p>
     * The message is formatted as:
     * <pre>
     *     [operationName] HTTP statusCode apiErrorMessage (apiErrorCode) [request-id: requestId]
     * </pre>
     * Example: [write] HTTP 400 type 'invalid_type' not found (validation_error) [request-id: abc-123]
     * </p>
     *
     * @return the formatted error message string
     */
    @Override
    public String getMessage() {
        // Use apiErrorMessage if available, otherwise fall back to the original message
        String message = (apiErrorMessage != null && !apiErrorMessage.isEmpty()) ? apiErrorMessage : super.getMessage();

        StringBuilder sb = new StringBuilder();

        // [operationName]
        if (operationName != null && !operationName.isEmpty()) {
            sb.append("[").append(operationName).append("] ");
        }

        // HTTP 400
        sb.append("HTTP ").append(getStatusCode()).append(" ");

        // type 'invalid_type' not found
        if (message != null && !message.isEmpty()) {
            sb.append(message);
        }

        // (validation_error)
        if (apiErrorCode != null && !apiErrorCode.isEmpty()) {
            sb.append(" (").append(apiErrorCode).append(")");
        }

        // [request-id: abc-123]
        if (requestId != null && !requestId.isEmpty()) {
            sb.append(" [request-id: ").append(requestId).append("]");
        }

        return sb.toString().trim();
    }

    // --- Helper Methods ---

    /**
     * Checks if this is a validation error.
     * Reliable error type checking based on error code.
     *
     * @return true if this is a validation error
     */
    public boolean isValidationError() {
        return "validation_error".equals(apiErrorCode);
    }

    /**
     * Checks if this is a not found (404) error.
     *
     * @return true if this is a 404 error
     */
    public boolean isNotFoundError() {
        return getStatusCode() == NOT_FOUND;
    }

    /**
     * Checks if this is an authentication (401) error.
     *
     * @return true if this is a 401 error
     */
    public boolean isAuthenticationError() {
        return getStatusCode() == UNAUTHORIZED;
    }

    /**
     * Checks if this is a rate limit (429) error.
     *
     * @return true if this is a rate limit error
     */
    public boolean isRateLimitError() {
        return getStatusCode() == TOO_MANY_REQUESTS || "rate_limit_exceeded".equals(apiErrorCode);
    }

    /**
     * Checks if this error should be retried.
     * 429 (Rate Limit) and 5xx (Server Errors) are typically retryable.
     *
     * @return true if this error is retryable
     */
    public boolean isRetryable() {
        int status = getStatusCode();
        // 429 (Rate Limit) and 5xx (Server Errors) are typically retryable.
        return status == TOO_MANY_REQUESTS || (status >= 500 && status < 600);
    }

    /**
     * Checks if this is a client error (4xx).
     *
     * @return true if this is a 4xx error
     */
    public boolean isClientError() {
        int status = getStatusCode();
        return status >= 400 && status < 500;
    }

    /**
     * Checks if this is a server error (5xx).
     *
     * @return true if this is a 5xx error
     */
    public boolean isServerError() {
        int status = getStatusCode();
        return status >= 500 && status < 600;
    }
}
