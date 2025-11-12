package dev.openfga.sdk.errors;

import static org.assertj.core.api.Assertions.assertThat;

import dev.openfga.sdk.api.configuration.ClientConfiguration;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

/**
 * Integration tests that verify the complete error handling flow,
 * especially the getMessage() override behavior when wrapped in ExecutionException.
 */
class FgaErrorIntegrationTest {

    @Test
    void testErrorMessageVisibilityInExecutionException() throws Exception {
        // Given - Simulate a validation error from the API
        String responseBody = "{\"code\":\"validation_error\",\"message\":\"type 'invalid_type' not found\"}";
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/stores/01ARZ3NDEKTSV4RRFFQ69G5FAV/check"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);
        assertThat(errorOpt).isPresent();
        FgaError fgaError = errorOpt.get();

        // When - Simulate wrapping in CompletableFuture (as the SDK does)
        CompletableFuture<String> future = CompletableFuture.failedFuture(fgaError);

        // Then - Verify the error message is visible even when wrapped
        try {
            future.get();
            throw new AssertionError("Expected ExecutionException to be thrown");
        } catch (ExecutionException e) {
            // This is what developers will see in their IDE and logs
            String exceptionMessage = e.getMessage();

            // Before Phase 2: Would have shown "dev.openfga.sdk.errors.FgaApiValidationError: check"
            // After Phase 2: Shows the actual error
            assertThat(exceptionMessage).contains("type 'invalid_type' not found");
            assertThat(exceptionMessage).contains("validation_error");
            assertThat(exceptionMessage).contains("check");

            // Verify cause is properly accessible
            assertThat(e.getCause()).isInstanceOf(FgaApiValidationError.class);
            FgaApiValidationError validationError = (FgaApiValidationError) e.getCause();
            assertThat(validationError.getInvalidField()).isEqualTo("type");
            assertThat(validationError.getInvalidValue()).isEqualTo("invalid_type");
        }
    }

    @Test
    void testCompleteErrorContext() {
        // Given - A fully populated error response
        String responseBody = "{\"code\":\"relation_not_found\",\"message\":\"relation 'document#editor' not found\"}";
        Map<String, List<String>> headers = Map.of(
                "X-Request-Id", List.of("550e8400-e29b-41d4-a716-446655440000"),
                "Retry-After", List.of("30"));
        HttpResponse<String> response = createMockResponse(400, responseBody, headers);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/stores/01ARZ3NDEKTSV4RRFFQ69G5FAV/check"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);

        // Then - Verify all error context is available
        assertThat(errorOpt).isPresent();
        FgaApiValidationError error = (FgaApiValidationError) errorOpt.get();

        // Basic error info
        assertThat(error.getStatusCode()).isEqualTo(400);
        assertThat(error.getMethod()).isEqualTo("POST");
        assertThat(error.getRequestUrl()).isEqualTo("http://localhost:8080");

        // Parsed error details
        assertThat(error.getOperationName()).isEqualTo("check");
        assertThat(error.getApiErrorCode()).isEqualTo("relation_not_found");
        assertThat(error.getApiErrorMessage()).isEqualTo("relation 'document#editor' not found");

        // Headers
        assertThat(error.getRequestId()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(error.getRetryAfterHeader()).isEqualTo("30");

        // Validation-specific parsing
        assertThat(error.getInvalidField()).isEqualTo("relation");
        assertThat(error.getInvalidValue()).isEqualTo("document#editor");
        assertThat(error.getMetadata()).containsEntry("invalid_field", "relation");
        assertThat(error.getMetadata()).containsEntry("invalid_value", "document#editor");

        // Formatted messages
        assertThat(error.getMessage()).isEqualTo("[check] relation 'document#editor' not found (relation_not_found)");

        assertThat(error.getDetailedMessage())
                .contains("[check]")
                .contains("relation 'document#editor' not found")
                .contains("(code: relation_not_found)")
                .contains("[request-id: 550e8400-e29b-41d4-a716-446655440000]")
                .contains("[HTTP 400]");

        assertThat(error.toString())
                .startsWith("FgaApiValidationError")
                .contains("[check]")
                .contains("relation 'document#editor' not found")
                .contains("(HTTP 400)")
                .contains("[code: relation_not_found]")
                .contains("[request-id: 550e8400-e29b-41d4-a716-446655440000]");
    }

    @Test
    void testMetadataExtensibility() {
        // Given
        String responseBody = "{\"code\":\"validation_error\",\"message\":\"invalid tuple\"}";
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();

        // When - Add custom metadata for application-specific error tracking
        error.addMetadata("retry_attempt", 1);
        error.addMetadata("user_id", "user:123");
        error.addMetadata("store_id", "01ARZ3NDEKTSV4RRFFQ69G5FAV");
        error.addMetadata("client_version", "1.0.0");

        // Then - Metadata is accessible
        Map<String, Object> metadata = error.getMetadata();
        assertThat(metadata).hasSize(4);
        assertThat(metadata).containsEntry("retry_attempt", 1);
        assertThat(metadata).containsEntry("user_id", "user:123");
        assertThat(metadata).containsEntry("store_id", "01ARZ3NDEKTSV4RRFFQ69G5FAV");
        assertThat(metadata).containsEntry("client_version", "1.0.0");

        // Metadata can be used for structured logging
        assertThat(metadata.get("retry_attempt")).isEqualTo(1);
    }

    @Test
    void testValidationErrorPatternMatching() {
        // Test all supported validation error patterns

        // Pattern 1: type 'X' not found
        testPattern("{\"code\":\"validation_error\",\"message\":\"type 'user' not found\"}", "type", "user");

        // Pattern 2: relation 'X' not found
        testPattern(
                "{\"code\":\"validation_error\",\"message\":\"relation 'document#viewer' not found\"}",
                "relation",
                "document#viewer");

        // Pattern 3: invalid CheckRequestTupleKey.Field
        testPatternFieldOnly(
                "{\"code\":\"validation_error\",\"message\":\"invalid CheckRequestTupleKey.Object: value does not match regex\"}",
                "Object");

        // Pattern 4: invalid TupleKey.Field
        testPatternFieldOnly(
                "{\"code\":\"validation_error\",\"message\":\"invalid TupleKey.Relation: value does not match regex\"}",
                "Relation");

        // Pattern 5: field must not be empty
        testPatternFieldOnly("{\"code\":\"validation_error\",\"message\":\"user must not be empty\"}", "user");
    }

    @Test
    void testDifferentErrorTypes() {
        // Test that different HTTP status codes create the right error types

        // 400/422 -> FgaApiValidationError
        testErrorType(400, "{\"code\":\"validation_error\",\"message\":\"invalid\"}", FgaApiValidationError.class);

        // 401 -> FgaApiAuthenticationError
        testErrorType(401, "{\"code\":\"unauthorized\",\"message\":\"auth failed\"}", FgaApiAuthenticationError.class);

        // 403 -> FgaApiAuthenticationError
        testErrorType(403, "{\"code\":\"forbidden\",\"message\":\"access denied\"}", FgaApiAuthenticationError.class);

        // 404 -> FgaApiNotFoundError
        testErrorType(404, "{\"code\":\"not_found\",\"message\":\"store not found\"}", FgaApiNotFoundError.class);

        // 429 -> FgaApiRateLimitExceededError
        testErrorType(
                429, "{\"code\":\"rate_limit\",\"message\":\"too many requests\"}", FgaApiRateLimitExceededError.class);

        // 500+ -> FgaApiInternalError
        testErrorType(500, "{\"code\":\"internal_error\",\"message\":\"server error\"}", FgaApiInternalError.class);
    }

    @Test
    void testErrorMessageFormattingVariations() {
        // Test getMessage() formatting with different combinations of available data

        // Full data: operation + message + code
        FgaError error1 =
                createError("{\"code\":\"validation_error\",\"message\":\"type 'user' not found\"}", 400, "check");
        assertThat(error1.getMessage()).isEqualTo("[check] type 'user' not found (validation_error)");

        // Message without code
        FgaError error2 = createError("{\"message\":\"something went wrong\"}", 500, "write");
        assertThat(error2.getMessage()).isEqualTo("[write] something went wrong");

        // No message (fallback to operation name)
        FgaError error3 = createError("{\"code\":\"error\"}", 500, "read");
        assertThat(error3.getMessage()).isEqualTo("read");

        // Empty body (fallback to operation name)
        FgaError error4 = createError("", 500, "listStores");
        assertThat(error4.getMessage()).isEqualTo("listStores");
    }

    // Helper methods

    private void testPattern(String responseBody, String expectedField, String expectedValue) {
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);
        assertThat(errorOpt).isPresent();
        assertThat(errorOpt.get()).isInstanceOf(FgaApiValidationError.class);

        FgaApiValidationError error = (FgaApiValidationError) errorOpt.get();
        assertThat(error.getInvalidField()).isEqualTo(expectedField);
        assertThat(error.getInvalidValue()).isEqualTo(expectedValue);
    }

    private void testPatternFieldOnly(String responseBody, String expectedField) {
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);
        assertThat(errorOpt).isPresent();
        assertThat(errorOpt.get()).isInstanceOf(FgaApiValidationError.class);

        FgaApiValidationError error = (FgaApiValidationError) errorOpt.get();
        assertThat(error.getInvalidField()).isEqualTo(expectedField);
    }

    private void testErrorType(int statusCode, String responseBody, Class<? extends FgaError> expectedType) {
        HttpResponse<String> response = createMockResponse(statusCode, responseBody, Map.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();
        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        Optional<FgaError> errorOpt = FgaError.getError("test", request, config, response, null);
        assertThat(errorOpt).isPresent();
        assertThat(errorOpt.get()).isInstanceOf(expectedType);
    }

    private FgaError createError(String responseBody, int statusCode, String operationName) {
        HttpResponse<String> response = createMockResponse(statusCode, responseBody, Map.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        Optional<FgaError> errorOpt = FgaError.getError(operationName, request, config, response, null);
        assertThat(errorOpt).isPresent();
        return errorOpt.get();
    }

    private HttpResponse<String> createMockResponse(int statusCode, String body, Map<String, List<String>> headers) {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return statusCode;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.of(headers, (k, v) -> true);
            }

            @Override
            public String body() {
                return body;
            }

            @Override
            public Optional<javax.net.ssl.SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return URI.create("http://localhost:8080/test");
            }

            @Override
            public HttpClient.Version version() {
                return HttpClient.Version.HTTP_1_1;
            }
        };
    }
}
