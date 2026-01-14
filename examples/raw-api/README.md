# Raw API Example

Demonstrates using the Raw API to call OpenFGA endpoints that are not yet wrapped by the SDK.

## What is the Raw API?

The Raw API provides direct HTTP access to OpenFGA endpoints while maintaining the SDK's configuration (authentication, telemetry, retries, and error handling).

Use cases:
- Calling endpoints not yet wrapped by the SDK
- Using an SDK version that lacks support for a particular endpoint
- Accessing custom endpoints that extend the OpenFGA API

## Prerequisites

- Java 11 or higher
- OpenFGA server running on `http://localhost:8080` (or set `FGA_API_URL`)

## Running

```bash
# From the SDK root directory, build the SDK first
./gradlew build

# Then run the example
cd examples/raw-api
./gradlew run
```

Or using the Makefile:

```bash
make build
make run
```

## What it does

The example demonstrates Raw API capabilities:

1. **POST Request with Typed Response**: Sends a request and deserializes the response into a custom Java class
2. **GET Request with Raw JSON**: Retrieves the raw JSON response string
3. **Query Parameters**: Adds query parameters to the request
4. **Custom Headers**: Adds custom HTTP headers to the request
5. **Error Handling**: Demonstrates SDK error handling and retry behavior

## Key Features

### Request Building

Build requests using the builder pattern:

```java
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/custom-endpoint")
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
client.raw().send(request, CustomResponse.class)
    .thenAccept(response -> {
        System.out.println("Data: " + response.getData());
        System.out.println("Status: " + response.getStatusCode());
    });
```

**Raw JSON Response:**
```java
client.raw().send(request)
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

- `RawApiExample.java`: Example demonstrating Raw API usage
- Custom response classes: `BulkDeleteResponse` and `ExperimentalFeatureResponse`

## Notes

This example uses placeholder endpoints for demonstration. In production:
- Use actual OpenFGA endpoint paths
- Match request/response structures to the target API
- Implement appropriate error handling

## See Also

- [Raw API Documentation](../../docs/RawApi.md)
- [OpenFGA API Reference](https://openfga.dev/api)

