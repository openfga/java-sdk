# API Executor

Direct HTTP access to OpenFGA endpoints — for both standard and streaming responses.

## Quick Start

### Standard (non-streaming) endpoint

```java
OpenFgaClient client = new OpenFgaClient(config);

ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/check")
    .pathParam("store_id", storeId)
    .body(Map.of("tuple_key", Map.of("user", "user:jon", "relation", "reader", "object", "doc:1")))
    .build();

// Typed response
ApiResponse<CheckResponse> response = client.apiExecutor().send(request, CheckResponse.class).get();

// Raw JSON
ApiResponse<String> rawResponse = client.apiExecutor().send(request).get();
```

### Streaming endpoint

```java
// Just pass the response class — no TypeReference boilerplate needed
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
    .body(listObjectsRequest)
    .build();

client.streamingApiExecutor(StreamedListObjectsResponse.class)
    .stream(
        request,
        response -> System.out.println("Object: " + response.getObject()),
        error    -> System.err.println("Error: " + error.getMessage())
    )
    .thenRun(() -> System.out.println("Done"));
```

## API Reference

### ApiExecutorRequestBuilder

Shared by both `ApiExecutor` and `StreamingApiExecutor`.

**Factory:**
```java
ApiExecutorRequestBuilder.builder(HttpMethod method, String path)
```

**Methods:**
```java
.pathParam(String key, String value)      // Replace {key} in path, URL-encoded
.queryParam(String key, String value)     // Add query parameter, URL-encoded
.header(String key, String value)         // Add HTTP header
.body(Object body)                        // Set request body (auto-serialized to JSON)
.build()                                  // Complete the builder (required)
```

### ApiExecutor

**Access:**
```java
ApiExecutor executor = client.apiExecutor();
```

**Methods:**
```java
CompletableFuture<ApiResponse<String>> send(ApiExecutorRequestBuilder request)
CompletableFuture<ApiResponse<T>>      send(ApiExecutorRequestBuilder request, Class<T> responseType)
```

### StreamingApiExecutor\<T\>

For streaming endpoints. Each response object is delivered to a consumer callback as it arrives.

**Access — preferred (concrete response types):**
```java
StreamingApiExecutor<MyResponse> executor = client.streamingApiExecutor(MyResponse.class);
```

**Access — escape hatch (when T is itself generic):**
```java
TypeReference<StreamResult<MyResponse>> typeRef = new TypeReference<StreamResult<MyResponse>>() {};
StreamingApiExecutor<MyResponse> executor = client.streamingApiExecutor(typeRef);
```

**Methods:**
```java
CompletableFuture<Void> stream(ApiExecutorRequestBuilder request, Consumer<T> consumer)
CompletableFuture<Void> stream(ApiExecutorRequestBuilder request, Consumer<T> consumer, Consumer<Throwable> errorConsumer)
```

- The `consumer` is invoked once per successfully parsed `{"result": ...}` line.
- The optional `errorConsumer` is invoked for `{"error": ...}` lines or HTTP errors.
- The returned `CompletableFuture<Void>` completes when the stream is exhausted or fails exceptionally on unrecoverable error.

### ApiResponse\<T\>

Returned by `ApiExecutor.send(...)`.

```java
int getStatusCode()                    // HTTP status
Map<String, List<String>> getHeaders() // Response headers
String getRawResponse()                // Raw JSON body
T getData()                            // Deserialized data
```

## Examples

### GET Request (ApiExecutor)
```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.GET, "/stores/{store_id}/feature")
    .pathParam("store_id", storeId)
    .build();

client.apiExecutor().send(request, FeatureResponse.class)
    .thenAccept(r -> System.out.println("Status: " + r.getStatusCode()));
```

### POST with Body (ApiExecutor)
```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/bulk-delete")
    .pathParam("store_id", storeId)
    .queryParam("force", "true")
    .body(new BulkDeleteRequest("2023-01-01", "user", 1000))
    .build();

client.apiExecutor().send(request, BulkDeleteResponse.class).get();
```

### Raw JSON Response (ApiExecutor)
```java
ApiResponse<String> response = client.apiExecutor().send(request).get();
String json = response.getRawResponse();
```

### Streaming endpoint (StreamingApiExecutor)
```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
    .body(new ListObjectsRequest().user("user:anne").relation("viewer").type("document"))
    .build();

List<String> objects = new ArrayList<>();
client.streamingApiExecutor(StreamedListObjectsResponse.class)
    .stream(request, response -> objects.add(response.getObject()))
    .thenRun(() -> System.out.println("Received " + objects.size() + " objects"));
```

### Query Parameters
```java
ApiExecutorRequestBuilder.builder(HttpMethod.GET, "/stores/{store_id}/items")
    .pathParam("store_id", storeId)
    .queryParam("page", "1")
    .queryParam("limit", "50")
    .queryParam("sort", "created_at")
    .build();
```

### Custom Headers
```java
ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/action")
    .header("X-Request-ID", UUID.randomUUID().toString())
    .header("X-Idempotency-Key", "key-123")
    .body(data)
    .build();
```

### Error Handling (ApiExecutor)
```java
client.apiExecutor().send(request, ResponseType.class)
    .exceptionally(e -> {
        if (e.getCause() instanceof FgaError) {
            FgaError error = (FgaError) e.getCause();
            System.err.println("API Error: " + error.getStatusCode());
        }
        return null;
    });
```

### Map as Request Body
```java
ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/settings")
    .pathParam("store_id", storeId)
    .body(Map.of(
        "setting", "value",
        "enabled", true,
        "threshold", 100,
        "options", List.of("opt1", "opt2")
    ))
    .build();
```

## Notes

- Path/query parameters are URL-encoded automatically
- Authentication tokens injected from client config
- `{store_id}` auto-replaced if not provided via `.pathParam()`
- `StreamingApiExecutor` requires a `TypeReference<StreamResult<T>>` because Java generics are erased at runtime; the type reference preserves the type information needed for JSON deserialization

## Migration to Typed Methods

When the SDK adds typed methods for an endpoint, you can migrate from API Executor:

```java
// API Executor
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/check")
    .body(req)
    .build();

client.apiExecutor().send(request, CheckResponse.class).get();

// Typed SDK (when available)
client.check(req).get();
```

