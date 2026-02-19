# API Executor Examples

Demonstrates using the **API Executor** and **Streaming API Executor** to call OpenFGA endpoints that are not yet wrapped by the SDK.

## What are the API Executors?

Both executors give you direct HTTP access to OpenFGA endpoints while maintaining the SDK's full client configuration (authentication, telemetry, retries, and error handling).

| | `ApiExecutor` | `StreamingApiExecutor` |
|---|---|---|
| **For** | Endpoints returning a single JSON response | Streaming endpoints |
| **Returns** | `CompletableFuture<ApiResponse<T>>` | `CompletableFuture<Void>` + per-object consumer callback |
| **Access** | `client.apiExecutor()` | `client.streamingApiExecutor(MyResponse.class)` |

Use cases:
- Calling endpoints not yet supported by the SDK
- Using an SDK version that lacks support for a particular endpoint
- Accessing custom or experimental endpoints that extend the OpenFGA API

## Prerequisites

- Java 17 or higher
- OpenFGA server running on `http://localhost:8080` (or set `FGA_API_URL`)

## Running

```bash
# Start OpenFGA server first (if not already running)
docker run -p 8080:8080 openfga/openfga run

# From the SDK root directory, build the SDK
./gradlew build

# Run the standard (non-streaming) example
cd examples/api-executor
make run

# Run the streaming example
make run-streaming
```

## Examples

### `ApiExecutorExample.java` — standard (non-streaming) endpoints

Demonstrates `ApiExecutor` against real OpenFGA endpoints:

1. **List Stores (GET, typed response)** — deserializes into `ListStoresResponse`
2. **Get Store (GET, raw JSON)** — returns the raw JSON string
3. **List Stores with Pagination** — demonstrates query parameters
4. **Create Store (POST, custom headers)** — custom HTTP headers
5. **Error Handling** — handles a 404 gracefully

### `StreamingApiExecutorExample.java` — streaming endpoints

Demonstrates `StreamingApiExecutor` against the `streamed-list-objects` endpoint:

1. Creates a temporary store and writes an authorization model
2. Writes 200 relationship tuples (100 owners + 100 viewers)
3. Calls `POST /stores/{store_id}/streamed-list-objects` via `client.streamingApiExecutor(StreamedListObjectsResponse.class).stream(request, consumer)`
4. Receives each object via a consumer callback as it arrives
5. Cleans up the store

## Key Features

### Standard request building (ApiExecutor)

```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/custom-endpoint")
    .pathParam("store_id", storeId)
    .queryParam("page_size", "20")
    .body(requestBody)
    .header("X-Custom-Header", "value")
    .build();

// Typed response
ApiResponse<CustomResponse> response = client.apiExecutor().send(request, CustomResponse.class).get();

// Raw JSON
ApiResponse<String> raw = client.apiExecutor().send(request).get();
```

### Streaming request (StreamingApiExecutor)

```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-endpoint")
    .body(requestBody)
    .build();

client.streamingApiExecutor(MyStreamedResponse.class)
    .stream(
        request,
        response -> System.out.println("Got: " + response),   // per-object callback
        error    -> System.err.println("Error: " + error)     // optional error callback
    )
    .thenRun(() -> System.out.println("Stream complete"));
```

If your response type is itself generic, use the `TypeReference` overload:
```java
TypeReference<StreamResult<MyStreamedResponse>> typeRef = new TypeReference<StreamResult<MyStreamedResponse>>() {};
client.streamingApiExecutor(typeRef).stream(request, consumer);
```

### SDK Features Applied

Both executors automatically include:
- Authentication credentials
- Retry logic for 5xx errors with exponential backoff
- Error handling and exception mapping
- Configured timeouts and headers
- Telemetry hooks
- Automatic `{store_id}` substitution from client configuration

## Code Structure

- `ApiExecutorExample.java` — standard (non-streaming) API Executor usage
- `StreamingApiExecutorExample.java` — streaming API Executor usage via the `streamed-list-objects` endpoint

## See Also

- [API Executor Documentation](../../docs/ApiExecutor.md)
- [OpenFGA API Reference](https://openfga.dev/api)

