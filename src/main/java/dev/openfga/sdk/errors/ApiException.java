package dev.openfga.sdk.errors;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

public class ApiException extends Exception {

    private int statusCode = 0;
    private HttpHeaders responseHeaders = null;
    private String responseData = null;

    public ApiException() {}

    public ApiException(Throwable throwable) {
        super(throwable);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(
            String message, Throwable throwable, int statusCode, HttpHeaders responseHeaders, String responseBody) {
        super(message, throwable);
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.responseData = responseBody;
    }

    public ApiException(String message, int statusCode, HttpHeaders responseHeaders, String responseBody) {
        this(message, (Throwable) null, statusCode, responseHeaders, responseBody);
    }

    public ApiException(String message, Throwable throwable, int statusCode, HttpHeaders responseHeaders) {
        this(message, throwable, statusCode, responseHeaders, null);
    }

    public ApiException(int statusCode, HttpHeaders responseHeaders, String responseBody) {
        this((String) null, (Throwable) null, statusCode, responseHeaders, responseBody);
    }

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(int statusCode, String message, HttpHeaders responseHeaders, String responseBody) {
        this(statusCode, message);
        this.responseHeaders = responseHeaders;
        this.responseData = responseBody;
    }

    public ApiException(String operationId, HttpResponse<String> response) {
        this(
                response.statusCode(),
                formatExceptionMessage(operationId, response.statusCode(), response.body()),
                response.headers(),
                response.body());
    }

    /**
     * Get the HTTP status code.
     *
     * @return HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the HTTP response headers.
     *
     * @return Headers as an HttpHeaders object
     */
    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Get the HTTP response body.
     *
     * @return Response body in the form of string
     */
    public String getResponseData() {
        return responseData;
    }

    private static String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }
}
