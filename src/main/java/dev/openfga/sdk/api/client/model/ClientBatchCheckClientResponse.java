package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.model.CheckResponse;
import dev.openfga.sdk.errors.FgaError;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

public class ClientBatchCheckClientResponse extends CheckResponse {
    private final ClientCheckRequest request;
    private final Throwable throwable;
    private final Integer statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientBatchCheckClientResponse(
            ClientCheckRequest request, ClientCheckResponse clientCheckResponse, Throwable throwable) {
        this.request = request;
        this.throwable = throwable;

        Throwable cause = throwable instanceof CompletionException || throwable instanceof ExecutionException
                ? throwable.getCause()
                : throwable;

        if (clientCheckResponse != null) {
            this.statusCode = clientCheckResponse.getStatusCode();
            this.headers = clientCheckResponse.getHeaders();
            this.rawResponse = clientCheckResponse.getRawResponse();
            this.setAllowed(clientCheckResponse.getAllowed());
            this.setResolution(clientCheckResponse.getResolution());
        } else if (cause instanceof FgaError) {
            FgaError error = (FgaError) cause;
            this.statusCode = error.getStatusCode();
            var responseHeaders = error.getResponseHeaders();
            this.headers = responseHeaders != null ? responseHeaders.map() : null;
            this.rawResponse = error.getResponseData();
        } else {
            // no HTTP response available, e.g. the request never reached the server
            this.statusCode = null;
            this.headers = null;
            this.rawResponse = null;
        }
    }

    public ClientCheckRequest getRequest() {
        return request;
    }

    /**
     * Returns the result of the check.
     * <p>
     * If the HTTP request was unsuccessful, this result will be null. If this is the case, you can examine the
     * original request with {@link ClientBatchCheckClientResponse#getRequest()} and the exception with
     * {@link ClientBatchCheckClientResponse#getThrowable()}.
     *
     * @return the check result. Is null if the HTTP request was unsuccessful.
     */
    @Override
    public Boolean getAllowed() {
        return super.getAllowed();
    }

    /**
     * Returns the caught exception if the HTTP request was unsuccessful.
     * <p>
     * If the HTTP request was unsuccessful, this result will be null. If this is the case, you can examine the
     * original request with {@link ClientBatchCheckClientResponse#getRequest()} and the exception with
     * {@link ClientBatchCheckClientResponse#getThrowable()}.
     *
     * @return the caught exception. Is null if the HTTP request was successful.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Returns the HTTP status code of the check response.
     * <p>
     * If no HTTP response was received — for example, the request never reached the server because of a
     * network failure (connection refused, timeout, DNS failure) and all retries were exhausted — this
     * returns {@code null}. In that case the underlying cause can be examined with
     * {@link ClientBatchCheckClientResponse#getThrowable()}.
     *
     * @return the HTTP status code, or {@code null} if no HTTP response was received.
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * @return the HTTP response headers, or {@code null} if no HTTP response was received.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * @return the raw HTTP response body, or {@code null} if no HTTP response was received.
     */
    public String getRawResponse() {
        return rawResponse;
    }

    public String getRelation() {
        return request == null ? null : request.getRelation();
    }

    public static BiFunction<ClientCheckResponse, Throwable, ClientBatchCheckClientResponse> asyncHandler(
            ClientCheckRequest request) {
        return (response, throwable) -> new ClientBatchCheckClientResponse(request, response, throwable);
    }
}
