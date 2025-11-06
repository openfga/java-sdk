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
import java.util.Optional;

public class FgaError extends ApiException {
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

    public FgaError(String message, Throwable cause, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, cause, code, responseHeaders, responseBody);
    }

    public FgaError(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, code, responseHeaders, responseBody);
    }

    /**
     * Parse the API error response body to extract the error message and code.
     * @param methodName The API method name that was called
     * @param responseBody The response body JSON string
     * @return A descriptive error message
     */
    private static String parseErrorMessage(String methodName, String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return methodName;
        }

        try {
            JsonNode jsonNode = ERROR_MAPPER.readTree(responseBody);

            // Try to extract message field
            JsonNode messageNode = jsonNode.get("message");
            String message = (messageNode != null && !messageNode.isNull()) ? messageNode.asText() : null;

            // If we have a message, return it, otherwise fall back to method name
            if (message != null && !message.trim().isEmpty()) {
                return message;
            }
        } catch (Exception e) {
            // If parsing fails, fall back to the method name
            // This is intentional to ensure errors are still reported even if the response format is unexpected
        }

        return methodName;
    }

    /**
     * Extract the API error code from the response body.
     * @param responseBody The response body JSON string
     * @return The error code, or null if not found
     */
    private static String extractErrorCode(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return null;
        }

        try {
            JsonNode jsonNode = ERROR_MAPPER.readTree(responseBody);
            
            JsonNode codeNode = jsonNode.get("code");
            if (codeNode != null && !codeNode.isNull()) {
                return codeNode.asText();
            }
        } catch (Exception e) {
            // If parsing fails, return null
            // This is intentional - we still want to report the error even if we can't extract the code
        }

        return null;
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

        // Parse the error message from the response body
        final String errorMessage = parseErrorMessage(name, body);
        final FgaError error;

        if (status == BAD_REQUEST || status == UNPROCESSABLE_ENTITY) {
            error = new FgaApiValidationError(errorMessage, previousError, status, headers, body);
        } else if (status == UNAUTHORIZED || status == FORBIDDEN) {
            error = new FgaApiAuthenticationError(errorMessage, previousError, status, headers, body);
        } else if (status == NOT_FOUND) {
            error = new FgaApiNotFoundError(errorMessage, previousError, status, headers, body);
        } else if (status == TOO_MANY_REQUESTS) {
            error = new FgaApiRateLimitExceededError(errorMessage, previousError, status, headers, body);
        } else if (isServerError(status)) {
            error = new FgaApiInternalError(errorMessage, previousError, status, headers, body);
        } else {
            error = new FgaError(errorMessage, previousError, status, headers, body);
        }

        error.setMethod(request.method());
        error.setRequestUrl(configuration.getApiUrl());

        // Set the operation name
        error.setOperationName(name);

        // Extract and set API error code from response body
        String apiErrorCode = extractErrorCode(body);
        if (apiErrorCode != null) {
            error.setApiErrorCode(apiErrorCode);
        }

        // Set the API error message (same as what was parsed for the constructor)
        // This allows getMessage() to return a formatted version
        if (!errorMessage.equals(name)) {
            // Only set apiErrorMessage if we actually got a message from the API
            // (not just falling back to the operation name)
            error.setApiErrorMessage(errorMessage);
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
}