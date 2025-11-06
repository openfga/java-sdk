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
import org.junit.jupiter.api.Test;

class FgaErrorTest {

    @Test
    void shouldParseValidationErrorMessageFromResponseBody() {
        // Given
        String responseBody = "{\"code\":\"validation_error\",\"message\":\"invalid relation 'foo'\"}";
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error).isInstanceOf(FgaApiValidationError.class);
        assertThat(error.getMessage()).isEqualTo("invalid relation 'foo'");
        assertThat(error.getCode()).isEqualTo("validation_error");
        assertThat(error.getApiErrorCode()).isEqualTo("validation_error");
        assertThat(error.getStatusCode()).isEqualTo(400);
        assertThat(error.getMethod()).isEqualTo("POST");
    }

    @Test
    void shouldParseInternalErrorMessageFromResponseBody() {
        // Given
        String responseBody = "{\"code\":\"internal_error\",\"message\":\"database connection failed\"}";
        HttpResponse<String> response = createMockResponse(500, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error).isInstanceOf(FgaApiInternalError.class);
        assertThat(error.getMessage()).isEqualTo("database connection failed");
        assertThat(error.getCode()).isEqualTo("internal_error");
        assertThat(error.getStatusCode()).isEqualTo(500);
        assertThat(error.getMethod()).isEqualTo("GET");
    }

    @Test
    void shouldParseNotFoundErrorMessageFromResponseBody() {
        // Given
        String responseBody = "{\"code\":\"store_id_not_found\",\"message\":\"store not found\"}";
        HttpResponse<String> response = createMockResponse(404, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("getStore", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error).isInstanceOf(FgaApiNotFoundError.class);
        assertThat(error.getMessage()).isEqualTo("store not found");
        assertThat(error.getCode()).isEqualTo("store_id_not_found");
        assertThat(error.getStatusCode()).isEqualTo(404);
    }

    @Test
    void shouldFallBackToMethodNameWhenMessageIsMissing() {
        // Given
        String responseBody = "{\"code\":\"validation_error\"}";
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getMessage()).isEqualTo("write");
        assertThat(error.getCode()).isEqualTo("validation_error");
    }

    @Test
    void shouldFallBackToMethodNameWhenResponseBodyIsEmpty() {
        // Given
        String responseBody = "";
        HttpResponse<String> response = createMockResponse(500, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getMessage()).isEqualTo("write");
        assertThat(error.getCode()).isNull();
    }

    @Test
    void shouldFallBackToMethodNameWhenResponseBodyIsNotJson() {
        // Given
        String responseBody = "Server Error";
        HttpResponse<String> response = createMockResponse(500, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getMessage()).isEqualTo("write");
        assertThat(error.getCode()).isNull();
    }

    @Test
    void shouldExtractRequestIdFromHeaders() {
        // Given
        String responseBody = "{\"code\":\"validation_error\",\"message\":\"invalid tuple\"}";
        Map<String, List<String>> headers = Map.of("X-Request-Id", List.of("abc-123-def-456"));
        HttpResponse<String> response = createMockResponse(400, responseBody, headers);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getRequestId()).isEqualTo("abc-123-def-456");
    }

    @Test
    void shouldHandleUnprocessableEntityAsValidationError() {
        // Given
        String responseBody = "{\"code\":\"invalid_tuple\",\"message\":\"tuple validation failed\"}";
        HttpResponse<String> response = createMockResponse(422, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error).isInstanceOf(FgaApiValidationError.class);
        assertThat(error.getMessage()).isEqualTo("tuple validation failed");
        assertThat(error.getCode()).isEqualTo("invalid_tuple");
        assertThat(error.getStatusCode()).isEqualTo(422);
    }

    @Test
    void shouldHandleAuthenticationError() {
        // Given
        String responseBody = "{\"code\":\"auth_failed\",\"message\":\"authentication failed\"}";
        HttpResponse<String> response = createMockResponse(401, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("read", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error).isInstanceOf(FgaApiAuthenticationError.class);
        assertThat(error.getMessage()).isEqualTo("authentication failed");
        assertThat(error.getCode()).isEqualTo("auth_failed");
        assertThat(error.getStatusCode()).isEqualTo(401);
    }

    @Test
    void shouldHandleRateLimitError() {
        // Given
        String responseBody = "{\"code\":\"rate_limit_exceeded\",\"message\":\"too many requests\"}";
        HttpResponse<String> response = createMockResponse(429, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error).isInstanceOf(FgaApiRateLimitExceededError.class);
        assertThat(error.getMessage()).isEqualTo("too many requests");
        assertThat(error.getCode()).isEqualTo("rate_limit_exceeded");
        assertThat(error.getStatusCode()).isEqualTo(429);
    }

    @Test
    void shouldReturnEmptyForSuccessfulResponse() {
        // Given
        HttpResponse<String> response = createMockResponse(200, "{}", Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("read", request, config, response, null);

        // Then
        assertThat(errorOpt).isEmpty();
    }

    @Test
    void shouldSetApiErrorMessageWhenMessageIsParsedFromResponse() {
        // Given
        String responseBody = "{\"code\":\"validation_error\",\"message\":\"type 'invalid_type' not found\"}";
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getApiErrorMessage()).isEqualTo("type 'invalid_type' not found");
        assertThat(error.getApiErrorCode()).isEqualTo("validation_error");
        assertThat(error.getOperationName()).isEqualTo("check");
    }

    @Test
    void shouldNotSetApiErrorMessageWhenFallingBackToOperationName() {
        // Given
        String responseBody = "{\"code\":\"validation_error\"}";
        HttpResponse<String> response = createMockResponse(400, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getApiErrorMessage()).isNull(); // Should be null when falling back
        assertThat(error.getMessage()).isEqualTo("write"); // Falls back to operation name
        assertThat(error.getOperationName()).isEqualTo("write");
    }

    @Test
    void shouldSetOperationNameForAllErrorTypes() {
        // Given
        String responseBody = "{\"code\":\"store_not_found\",\"message\":\"store not found\"}";
        HttpResponse<String> response = createMockResponse(404, responseBody, Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("getStore", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getOperationName()).isEqualTo("getStore");
        assertThat(error.getApiErrorMessage()).isEqualTo("store not found");
    }

    @Test
    void shouldHandleAllFieldsWhenFullyPopulated() {
        // Given
        String responseBody = "{\"code\":\"rate_limit_exceeded\",\"message\":\"too many requests\"}";
        Map<String, List<String>> headers = Map.of(
                "X-Request-Id", List.of("req-123-456"),
                "Retry-After", List.of("60"));
        HttpResponse<String> response = createMockResponse(429, responseBody, headers);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("write", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getOperationName()).isEqualTo("write");
        assertThat(error.getApiErrorMessage()).isEqualTo("too many requests");
        assertThat(error.getApiErrorCode()).isEqualTo("rate_limit_exceeded");
        assertThat(error.getCode()).isEqualTo("rate_limit_exceeded"); // Alias method
        assertThat(error.getRequestId()).isEqualTo("req-123-456");
        assertThat(error.getRetryAfterHeader()).isEqualTo("60");
        assertThat(error.getMethod()).isEqualTo("POST");
        assertThat(error.getRequestUrl()).isEqualTo("http://localhost:8080");
    }

    @Test
    void shouldHandleEmptyBodyGracefully() {
        // Given
        HttpResponse<String> response = createMockResponse(500, "", Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("check", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getOperationName()).isEqualTo("check");
        assertThat(error.getApiErrorMessage()).isNull(); // No message in empty body
        assertThat(error.getApiErrorCode()).isNull(); // No code in empty body
        assertThat(error.getMessage()).isEqualTo("check"); // Falls back to operation name
    }

    @Test
    void shouldHandleNonJsonBodyGracefully() {
        // Given
        HttpResponse<String> response = createMockResponse(500, "Internal Server Error", Map.of());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test"))
                .GET()
                .build();

        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        // When
        Optional<FgaError> errorOpt = FgaError.getError("listStores", request, config, response, null);

        // Then
        assertThat(errorOpt).isPresent();
        FgaError error = errorOpt.get();
        assertThat(error.getOperationName()).isEqualTo("listStores");
        assertThat(error.getApiErrorMessage()).isNull(); // Can't parse non-JSON
        assertThat(error.getApiErrorCode()).isNull(); // Can't parse non-JSON
        assertThat(error.getMessage()).isEqualTo("listStores"); // Falls back to operation name
    }

    // Helper method to create mock HttpResponse
    private HttpResponse<String> createMockResponse(int statusCode, String body, Map<String, List<String>> headers) {
        return new HttpResponse<String>() {
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