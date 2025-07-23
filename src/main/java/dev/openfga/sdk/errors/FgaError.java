package dev.openfga.sdk.errors;

import static dev.openfga.sdk.errors.HttpStatusCode.*;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
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
        Optional<String> retryAfter = headers.firstValue("Retry-After");
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

    public void setRetryAfterHeader(String retryAfterHeader) {
        this.retryAfterHeader = retryAfterHeader;
    }

    public String getRetryAfterHeader() {
        return retryAfterHeader;
    }
}
