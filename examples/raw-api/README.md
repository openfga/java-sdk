# Raw API Example

Demonstrates using the Raw API to call OpenFGA endpoints that are not yet wrapped by the SDK.

## What is the Raw API?

The Raw API (also known as the "escape hatch") allows you to make HTTP calls to any OpenFGA endpoint while still benefiting from the SDK's configuration (authentication, telemetry, retries, and error handling).

This is useful when:
- You want to call a new endpoint that is not yet supported by the SDK
- You are using an earlier version of the SDK that doesn't yet support a particular endpoint
- You have a custom endpoint deployed that extends the OpenFGA API

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

The example demonstrates several key features of the Raw API:

1. **POST Request with Typed Response**: Calls a hypothetical `/bulk-delete` endpoint and deserializes the response into a custom Java class
2. **GET Request with Raw JSON**: Retrieves raw JSON response without automatic deserialization
3. **Query Parameters**: Shows how to add query parameters for filtering and pagination
4. **Custom Headers**: Demonstrates adding custom HTTP headers to requests
5. **Error Handling**: Shows how the Raw API benefits from the SDK's built-in error handling and retry logic

## Key Features

### Request Building

The Raw API uses a builder pattern to construct requests:

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

You can handle responses in two ways:

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

### Automatic Features

The Raw API automatically includes:
- Authentication credentials from your client configuration
- Retry logic for 5xx errors with exponential backoff
- Error handling and exception mapping
- Configured timeouts and headers
- Telemetry and observability hooks

## Code Structure

- `RawApiExample.java`: Main example class demonstrating various Raw API use cases
- Custom response classes: `BulkDeleteResponse` and `ExperimentalFeatureResponse`

## Notes

This example uses hypothetical endpoints for demonstration purposes. In a real-world scenario:
- Replace the endpoint paths with actual OpenFGA endpoints
- Adjust the request/response structures to match your API
- Handle errors appropriately for your use case

## See Also

- [Raw API Documentation](../../docs/RawApi.md)
- [OpenFGA API Reference](https://openfga.dev/api)

