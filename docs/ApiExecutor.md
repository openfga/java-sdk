# API Executor

Direct HTTP access to OpenFGA endpoints.

## Quick Start

```java
OpenFgaClient client = new OpenFgaClient(config);

// Build request
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/check")
    .pathParam("store_id", storeId)
    .body(Map.of("tuple_key", Map.of("user", "user:jon", "relation", "reader", "object", "doc:1")))
    .build();

// Execute - typed response
ApiResponse<CheckResponse> response = client.apiExecutor().send(request, CheckResponse.class).get();

// Execute - raw JSON
ApiResponse<String> rawResponse = client.apiExecutor().send(request).get();
```

## API Reference

### ApiExecutorRequestBuilder

**Factory:**
```java
ApiExecutorRequestBuilder.builder(String method, String path)
```

**Methods:**
```java
.pathParam(String key, String value)      // Replace {key} in path, URL-encoded
.queryParam(String key, String value)     // Add query parameter, URL-encoded
.header(String key, String value)         // Add HTTP header
.body(Object body)                        // Set request body (auto-serialized to JSON)
.build()                                  // Complete the builder (required)
```

**Example:**
```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/write")
    .pathParam("store_id", "01ABC")
    .queryParam("dry_run", "true")
    .header("X-Request-ID", "uuid")
    .body(requestObject)
    .build();
```

### ApiExecutor

**Access:**
```java
RawApi rawApi = client.apiExecutor();
```

**Methods:**
```java
CompletableFuture<ApiResponse<String>> send(ApiExecutorRequestBuilder request)
CompletableFuture<ApiResponse<T>> send(ApiExecutorRequestBuilder request, Class<T> responseType)
```

### ApiResponse<T>

```java
int getStatusCode()                    // HTTP status
Map<String, List<String>> getHeaders() // Response headers
String getRawResponse()                // Raw JSON body
T getData()                            // Deserialized data
```

## Examples

### GET Request
```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}/feature")
    .pathParam("store_id", storeId)
    .build();

client.apiExecutor().send(request, FeatureResponse.class)
    .thenAccept(r -> System.out.println("Status: " + r.getStatusCode()));
```

### POST with Body
```java
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/bulk-delete")
    .pathParam("store_id", storeId)
    .queryParam("force", "true")
    .body(new BulkDeleteRequest("2023-01-01", "user", 1000))
    .build();

client.apiExecutor().send(request, BulkDeleteResponse.class).get();
```

### Raw JSON Response
```java
ApiResponse<String> response = client.apiExecutor().send(request).get();
String json = response.getRawResponse(); // Raw JSON
```

### Query Parameters
```java
ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}/items")
    .pathParam("store_id", storeId)
    .queryParam("page", "1")
    .queryParam("limit", "50")
    .queryParam("sort", "created_at")
    .build();
```

### Custom Headers
```java
ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/action")
    .header("X-Request-ID", UUID.randomUUID().toString())
    .header("X-Idempotency-Key", "key-123")
    .body(data)
    .build();
```

### Error Handling
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
ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/settings")
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

## Migration to Typed Methods

When SDK adds typed methods for an endpoint, you can migrate from API Executor:

```java
// API Executor
ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/check")
    .body(req)
    .build();
    
client.apiExecutor().send(request, CheckResponse.class).get();

// Typed SDK (when available)
client.check(req).get();
```

