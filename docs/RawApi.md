# Raw API

Direct HTTP access to OpenFGA endpoints.

## Quick Start

```java
OpenFgaClient client = new OpenFgaClient(config);

// Build request
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/check")
    .pathParam("store_id", storeId)
    .body(Map.of("tuple_key", Map.of("user", "user:jon", "relation", "reader", "object", "doc:1")));

// Execute - typed response
ApiResponse<CheckResponse> response = client.raw().send(request, CheckResponse.class).get();

// Execute - raw JSON
ApiResponse<String> rawResponse = client.raw().send(request).get();
```

## API Reference

### RawRequestBuilder

**Factory:**
```java
RawRequestBuilder.builder(String method, String path)
```

**Methods:**
```java
.pathParam(String key, String value)      // Replace {key} in path, URL-encoded
.queryParam(String key, String value)     // Add query parameter, URL-encoded
.header(String key, String value)         // Add HTTP header
.body(Object body)                        // Set request body (auto-serialized to JSON)
```

**Example:**
```java
RawRequestBuilder.builder("POST", "/stores/{store_id}/write")
    .pathParam("store_id", "01ABC")
    .queryParam("dry_run", "true")
    .header("X-Request-ID", "uuid")
    .body(requestObject);
```

### RawApi

**Access:**
```java
RawApi rawApi = client.raw();
```

**Methods:**
```java
CompletableFuture<ApiResponse<String>> send(RawRequestBuilder request)
CompletableFuture<ApiResponse<T>> send(RawRequestBuilder request, Class<T> responseType)
```

### ApiResponse<T>

```java
int getStatusCode()                    // HTTP status
Map<String, List<String>> getHeaders() // Response headers
String getRawResponse()                // Raw JSON body
T getData()                            // Deserialized data
```

## Examples

### Typed Response
```java
RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/feature")
    .pathParam("store_id", storeId);

client.raw().send(request, FeatureResponse.class)
    .thenAccept(r -> System.out.println("Status: " + r.getStatusCode()));
```

### POST with Body
```java
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/bulk-delete")
    .pathParam("store_id", storeId)
    .queryParam("force", "true")
    .body(new BulkDeleteRequest("2023-01-01", "user", 1000));

client.raw().send(request, BulkDeleteResponse.class).get();
```

### Raw JSON Response
```java
ApiResponse<String> response = client.raw().send(request).get();
String json = response.getRawResponse(); // Raw JSON
```

### Query Parameters
```java
RawRequestBuilder.builder("GET", "/stores/{store_id}/items")
    .pathParam("store_id", storeId)
    .queryParam("page", "1")
    .queryParam("limit", "50")
    .queryParam("sort", "created_at");
```

### Custom Headers
```java
RawRequestBuilder.builder("POST", "/stores/{store_id}/action")
    .header("X-Request-ID", UUID.randomUUID().toString())
    .header("X-Idempotency-Key", "key-123")
    .body(data);
```

### Error Handling
```java
client.raw().send(request, ResponseType.class)
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
.body(Map.of(
    "setting", "value",
    "enabled", true,
    "threshold", 100,
    "options", List.of("opt1", "opt2")
))
```

## Notes

- Path/query parameters are URL-encoded automatically
- Authentication tokens injected from client config
- Retries on 429, 5xx errors
- `{store_id}` auto-replaced if not provided via `.pathParam()`

## Migration to Typed Methods

```java
// Raw API
client.raw().send(
    RawRequestBuilder.builder("POST", "/stores/{store_id}/check").body(req),
    CheckResponse.class
).get();

// Typed SDK (when available)
client.check(req).get();
```

