# API Executor Example

Demonstrates using the API Executor to call OpenFGA endpoints that are not yet wrapped by the SDK.

## What is the API Executor?

The API Executor provides direct HTTP access to OpenFGA endpoints while maintaining the SDK's configuration (authentication, telemetry, retries, and error handling).

Use cases:
- Calling endpoints not yet wrapped by the SDK
- Using an SDK version that lacks support for a particular endpoint
- Accessing custom endpoints that extend the OpenFGA API

## Prerequisites

- Java 11 or higher
- OpenFGA server running on `http://localhost:8080` (or set `FGA_API_URL`)

## Running

```bash
# Start OpenFGA server first (if not already running)
docker run -p 8080:8080 openfga/openfga run

# From the SDK root directory, build the SDK
./gradlew build

# Then run the example
cd examples/api-executor
./gradlew run
```

Or using the Makefile:

```bash
make build
make run
```

## What it does

The example demonstrates API Executor capabilities using real OpenFGA endpoints:

1. **List Stores (GET with typed response)**: Lists all stores and deserializes into `ListStoresResponse`
2. **Get Store (GET with raw JSON)**: Retrieves a single store and returns the raw JSON string
3. **List Stores with Pagination**: Demonstrates query parameters using `page_size`
4. **Create Store (POST with custom headers)**: Creates a new store with custom HTTP headers
5. **Error Handling**: Attempts to get a non-existent store and handles the 404 error properly

All requests will succeed (except #5 which intentionally triggers an error for demonstration).

## Key Features

### Request Building

Build requests using the builder pattern:

```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/custom-endpoint")
    .pathParam("store_id", storeId)
    .queryParam("page_size", "20")
    .queryParam("continuation_token", "eyJwayI6...")
    .body(requestBody)
    .header("X-Custom-Header", "value")
    .build();
```

### Response Handling


**Typed Response (automatic deserialization):**
```java
client.apiExecutor().send(request, CustomResponse.class)
    .thenAccept(response -> {
        System.out.println("Data: " + response.getData());
        System.out.println("Status: " + response.getStatusCode());
    });
```

**Raw JSON Response:**
```java
client.apiExecutor().send(request)
    .thenAccept(response -> {
        System.out.println("Raw JSON: " + response.getRawResponse());
    });
```

### SDK Features Applied

Requests automatically include:
- Authentication credentials
- Retry logic for 5xx errors with exponential backoff
- Error handling and exception mapping
- Configured timeouts and headers
- Telemetry hooks

## Code Structure

- `ApiExecutorExample.java`: Example demonstrating API Executor usage with real OpenFGA endpoints

## Notes

This example uses real OpenFGA endpoints (`/stores`, `/stores/{store_id}`) to demonstrate actual functionality. The API Executor can be used with any OpenFGA endpoint, including custom endpoints if you have extended the API.

## See Also

- [API Executor Documentation](../../docs/RawApi.md)
- [OpenFGA API Reference](https://openfga.dev/api)

