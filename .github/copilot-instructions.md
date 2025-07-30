# OpenFGA Java SDK - Agent Instructions

## Project Overview

This is the **OpenFGA Java SDK** - an auto-generated Java client library for OpenFGA (Fine-Grained Authorization system inspired by Google's Zanzibar). The SDK provides a comprehensive API for managing authorization models, relationship tuples, and performing authorization checks.

### Key Facts
- **Package**: `dev.openfga:openfga-sdk` (published to Maven Central)
- **Java Support**: Minimum JDK 11, tested on 11, 17, 21
- **Build System**: Gradle with comprehensive test suites
- **Generated Code**: Uses OpenAPI Generator - changes should go through sdk-generator repo first

## Architecture & Structure

### Core Components
```
src/main/java/dev/openfga/sdk/
├── api/                    # Core API classes (162 files)
│   ├── OpenFgaApi.java    # Main API class (118KB)
│   ├── client/            # Client implementation (38 files)
│   ├── configuration/     # Configuration classes (32 files)
│   ├── model/             # Data models (87 files)
│   └── auth/              # Authentication handling (4 files)
├── errors/                # Error handling (10 files)
├── telemetry/            # OpenTelemetry integration (9 files)
└── util/                 # Utility classes (3 files)
```

### Key Classes to Understand
- **`OpenFgaClient.java`**: Main client interface for all operations
- **`HttpRequestAttempt.java`**: HTTP request handling with retry logic
- **`Configuration.java`**: SDK configuration and settings
- **`FgaError.java`**: Error handling and response parsing
- **`HttpStatusCode.java`**: HTTP status code utilities and retry logic

## Development Guidelines

### Code Quality Standards
- **Formatting**: Use Spotless (`./gradlew fmt`)
- **Testing**: JUnit Jupiter, AssertJ, Mockito, WireMock, Testcontainers
- **Coverage**: JaCoCo for test coverage
- **Security**: Semgrep for security analysis
- **Style**: Follow existing patterns and Java conventions

### Testing Strategy
- **Unit Tests**: Isolated component testing
- **Integration Tests**: End-to-end scenarios with WireMock
- **Test Coverage**: Aim for comprehensive coverage of new features
- **Test Naming**: Use descriptive names that explain the scenario

### Dependencies
- **HTTP Client**: Java 11+ HTTP Client APIs
- **JSON**: Jackson for serialization/deserialization
- **Observability**: OpenTelemetry integration
- **Annotations**: JSR305 for null safety
- **Testing**: JUnit Jupiter, AssertJ, Mockito, WireMock

## Core Architecture Patterns

### Async-First Design
- **CompletableFuture**: All API operations return `CompletableFuture<T>` for non-blocking execution
- **HTTP Client**: Uses Java 11+ HTTP Client with connection pooling
- **Error Propagation**: Exceptions are properly wrapped and propagated through futures

### Configuration Management
- **Builder Pattern**: Configuration uses builder pattern for flexibility
- **Validation**: All configuration parameters are validated at construction time
- **Defaults**: Sensible defaults provided for all optional parameters

### Error Handling Strategy
- **Typed Exceptions**: Custom exception hierarchy under `dev.openfga.sdk.errors`
- **HTTP Error Mapping**: HTTP status codes mapped to appropriate exception types
- **Context Preservation**: Error messages include relevant context for debugging

## Code Review Guidelines

### What to Look For
1. **Backward Compatibility**: Ensure existing APIs continue to work
2. **Error Handling**: Proper exception handling and user-friendly error messages
3. **Thread Safety**: CompletableFuture usage and concurrent operations
4. **Resource Management**: HTTP client reuse and connection pooling
5. **Configuration**: Proper validation and default values
6. **Testing**: Comprehensive test coverage for new features
7. **Documentation**: JavaDoc for public APIs

### Common Patterns
- **Async Operations**: Heavy use of `CompletableFuture<T>`
- **Builder Pattern**: Configuration and request builders
- **Static Utilities**: Pure functions in utility classes
- **Error Wrapping**: Convert HTTP errors to `FgaError` objects
- **Validation**: Parameter validation with `assertParamExists()`

### Anti-Patterns to Avoid
- **Blocking Operations**: Don't block async operations
- **Hardcoded Values**: Use configuration or constants
- **Poor Error Messages**: Provide context and actionable information
- **Memory Leaks**: Properly handle HTTP client lifecycle
- **Breaking Changes**: Maintain API compatibility

## Implementation Guidelines

### Adding New Features
1. **Design First**: Consider backward compatibility and API surface
2. **Model Classes**: Add to `api/client/model/` package
3. **Tests**: Write comprehensive unit and integration tests
4. **Documentation**: Add JavaDoc and update README if needed
5. **Error Handling**: Proper exception types and error messages
6. **CHANGELOG**: Update `CHANGELOG.md` with new features and breaking changes

### Fixing Bugs
1. **Root Cause**: Understand the underlying issue, not just symptoms
2. **Test Coverage**: Add tests that reproduce the bug
3. **Minimal Changes**: Make the smallest change that fixes the issue
4. **Regression Testing**: Ensure existing functionality still works
5. **CHANGELOG**: Document bug fixes in `CHANGELOG.md`

### Refactoring Code
1. **Incremental**: Make small, focused changes
2. **Test Safety**: Ensure all tests pass after each change
3. **API Stability**: Don't break public APIs without major version bump
4. **Performance**: Consider impact on HTTP client performance
5. **CHANGELOG**: Document significant refactoring and any breaking changes

## Common Tasks

### Working with HTTP Requests
- Use `HttpRequestAttempt<T>` for all API calls
- Handle retries through `HttpStatusCode.isRetryable()`
- Parse responses with proper error handling
- Use telemetry for observability

### Working with Configuration
- Validate all configuration parameters
- Provide sensible defaults
- Support both programmatic and builder patterns
- Consider environment variable overrides

### Working with Models
- Follow OpenAPI generator patterns
- Use immutable objects where possible
- Implement proper `equals()`, `hashCode()`, `toString()`
- Add comprehensive validation

## Testing Patterns

### Unit Tests
```java
@Test
void shouldHandleValidInput() {
    // Given
    var input = createValidInput();
    
    // When
    var result = methodUnderTest(input);
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(expected);
}
```

### Integration Tests
```java
@Test
void shouldRetryOnServerError() throws Exception {
    // Given
    stubFor(post(urlEqualTo("/stores/test/write"))
        .willReturn(aResponse().withStatus(500)));
    
    // When & Then
    assertThatThrownBy(() -> client.write(request).get())
        .isInstanceOf(ExecutionException.class);
    
    verify(exactly(4), postRequestedFor(urlEqualTo("/stores/test/write")));
}
```

## Performance Considerations

- **HTTP Client Reuse**: Don't create new clients for each request
- **Connection Pooling**: Use appropriate connection pool settings
- **Async Operations**: Prefer non-blocking operations
- **Memory Usage**: Be mindful of large response handling
- **Retry Logic**: Implement exponential backoff to avoid thundering herd

## Security Considerations

- **API Keys**: Never log or expose API keys
- **Input Validation**: Validate all user inputs
- **Error Information**: Don't leak sensitive information in error messages
- **Dependencies**: Keep dependencies up to date for security patches

---

Remember: This is an auto-generated SDK, so architectural changes should be carefully considered and may need to be implemented in the upstream sdk-generator repository.
