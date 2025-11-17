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

    public static class ApiErrorResponse {
        public String code;
        public String message;
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
        } else if (isServerError(status)) {
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
            ObjectMapper mapper = new ObjectMapper();
            try {
                ApiErrorResponse resp = mapper.readValue(body, ApiErrorResponse.class);
                error.setApiErrorCode(resp.code);
                error.setApiErrorMessage(resp.message);
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

    @Override
    public String getMessage() {
        if (apiErrorMessage != null && !apiErrorMessage.isEmpty() && operationName != null) {
            String codePart = (apiErrorCode != null && !apiErrorCode.isEmpty()) ? " (" + apiErrorCode + ")" : "";
            return String.format("[%s] %s%s", operationName, apiErrorMessage, codePart);
        } else {
            return super.getMessage();
        }
    }
}
