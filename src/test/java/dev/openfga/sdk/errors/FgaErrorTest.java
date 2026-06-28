package dev.openfga.sdk.errors;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpHeaders;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FgaErrorTest {

    private static FgaError error(int status) {
        return new FgaError("original", status, HttpHeaders.of(Map.of(), (a, b) -> true), null);
    }

    // --- getMessage() format ---

    @Test
    void getMessage_withAllFields() {
        FgaError error = error(400);
        error.setOperationName("write");
        error.setApiErrorMessage("type 'invalid_type' not found");
        error.setApiErrorCode("validation_error");
        error.setRequestId("abc-123");

        assertThat(error.getMessage())
                .isEqualTo("[write] HTTP 400 type 'invalid_type' not found (validation_error) [request-id: abc-123]");
    }

    @Test
    void getMessage_withoutRequestId() {
        FgaError error = error(400);
        error.setOperationName("write");
        error.setApiErrorMessage("type not found");
        error.setApiErrorCode("validation_error");

        assertThat(error.getMessage()).isEqualTo("[write] HTTP 400 type not found (validation_error)");
    }

    @Test
    void getMessage_withoutOperationName() {
        FgaError error = error(400);
        error.setApiErrorMessage("type not found");
        error.setApiErrorCode("validation_error");
        error.setRequestId("abc-123");

        assertThat(error.getMessage()).isEqualTo("HTTP 400 type not found (validation_error) [request-id: abc-123]");
    }

    @Test
    void getMessage_withoutApiErrorCode() {
        FgaError error = error(400);
        error.setOperationName("write");
        error.setApiErrorMessage("some message");
        error.setRequestId("abc-123");

        assertThat(error.getMessage()).isEqualTo("[write] HTTP 400 some message [request-id: abc-123]");
    }

    @Test
    void getMessage_withOnlyStatusCode() {
        FgaError error = new FgaError(null, 500, HttpHeaders.of(Map.of(), (a, b) -> true), null);

        assertThat(error.getMessage()).isEqualTo("HTTP 500");
    }

    // --- helper method correctness ---

    @Test
    void isValidationError_trueWhenCodeMatches() {
        FgaError error = error(400);
        error.setApiErrorCode("validation_error");
        assertThat(error.isValidationError()).isTrue();
    }

    @Test
    void isValidationError_falseForOtherCode() {
        FgaError error = error(400);
        error.setApiErrorCode("auth_error");
        assertThat(error.isValidationError()).isFalse();
    }

    @Test
    void isUnknownError_trueWhenCodeIsUnknownError() {
        FgaError error = error(400);
        error.setApiErrorCode("unknown_error");
        assertThat(error.isUnknownError()).isTrue();
    }

    @Test
    void isRateLimitError_trueFor429() {
        FgaError error = error(429);
        assertThat(error.isRateLimitError()).isTrue();
    }

    @Test
    void isRateLimitError_trueForCode() {
        FgaError error = error(400);
        error.setApiErrorCode("rate_limit_exceeded");
        assertThat(error.isRateLimitError()).isTrue();
    }

    @Test
    void isRetryable_trueFor429() {
        assertThat(error(429).isRetryable()).isTrue();
    }

    @Test
    void isRetryable_trueFor500() {
        assertThat(error(500).isRetryable()).isTrue();
    }

    @Test
    void isRetryable_falseFor400() {
        assertThat(error(400).isRetryable()).isFalse();
    }

    @Test
    void isRetryable_falseFor501() {
        assertThat(error(501).isRetryable()).isFalse();
    }

    @Test
    void isClientError_trueFor4xx() {
        assertThat(error(400).isClientError()).isTrue();
        assertThat(error(422).isClientError()).isTrue();
        assertThat(error(429).isClientError()).isTrue();
    }

    @Test
    void isClientError_falseFor5xx() {
        assertThat(error(500).isClientError()).isFalse();
    }

    @Test
    void isServerError_trueFor5xx() {
        assertThat(error(500).isServerError()).isTrue();
        assertThat(error(503).isServerError()).isTrue();
    }

    @Test
    void isServerError_falseFor4xx() {
        assertThat(error(400).isServerError()).isFalse();
    }
}
