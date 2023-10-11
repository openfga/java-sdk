package dev.openfga.sdk.errors;

public class HttpStatusCode {
    private HttpStatusCode() {} // No need to instantiate.

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int TOO_MANY_REQUESTS = 429;
    public static final int NOT_IMPLEMENTED = 501;

    /**
     * Returns true for any 2XX HTTP status code.
     */
    public static boolean isSuccessful(int statusCode) {
        return isBetween(200, statusCode, 300);
    }

    /**
     * Returns true for any 5XX HTTP status code.
     */
    public static boolean isServerError(int statusCode) {
        return isBetween(500, statusCode, 600);
    }

    /**
     * Returns true for an HTTP status code that could reasonably be retried.
     */
    public static boolean isRetryable(int statusCode) {
        return statusCode == 429 || (isServerError(statusCode) && statusCode != NOT_IMPLEMENTED);
    }

    private static boolean isBetween(int min, int n, int maxExclusive) {
        return min <= n && n < maxExclusive;
    }
}
