# Raw API - Alternative Access

## Overview

The Raw API provides an alternative mechanism for calling OpenFGA endpoints that are not yet supported by the typed SDK methods. This is particularly useful for:

- **Experimental Features**: Access newly released or experimental OpenFGA endpoints before they're officially supported in the SDK
- **Beta Endpoints**: Test beta features without waiting for SDK updates
- **Custom Extensions**: Call custom or extended OpenFGA endpoints in your deployment
- **Rapid Prototyping**: Quickly integrate with new API features while SDK support is being developed

## Key Benefits

All requests made through the Raw API automatically benefit from the SDK's infrastructure:

- ✅ **Automatic Authentication** - Bearer token injection handled automatically
- ✅ **Configuration Adherence** - Respects base URLs, store IDs, and timeouts from your ClientConfiguration
- ✅ **Automatic Retries** - Built-in retry logic for 5xx errors and network failures
- ✅ **Consistent Error Handling** - Standard SDK exception handling for 400, 401, 404, 500 errors
- ✅ **Type Safety** - Option to deserialize responses into typed Java objects or work with raw JSON

## Quick Start

### Basic Usage

```java
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.RawRequestBuilder;
import dev.openfga.sdk.api.configuration.ClientConfiguration;

// Initialize the client
ClientConfiguration config = new ClientConfiguration()
    .apiUrl("http://localhost:8080")
    .storeId("01YCP46JKYM8FJCQ37NMBYHE5X");

OpenFgaClient fgaClient = new OpenFgaClient(config);

// Example: Use RawRequestBuilder to call the actual /stores/{store_id}/check endpoint
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/check")
    .pathParam("store_id", fgaClient.getStoreId())
    .body(Map.of(
        "tuple_key", Map.of(
            "user", "user:jon",
            "relation", "can_read",
            "object", "document:2026"
        ),
        "contextual_tuples", List.of()
    ));

// Get raw JSON response
fgaClient.raw().send(request)
    .thenAccept(response -> {
        System.out.println("Response: " + response.getRawResponse());
    });
```

## API Components

### 1. RawRequestBuilder

The `RawRequestBuilder` provides a fluent interface for constructing HTTP requests.

#### Factory Method

```java
RawRequestBuilder.builder(String method, String path)
```

- **method**: HTTP method (GET, POST, PUT, DELETE, PATCH, etc.)
- **path**: API path with optional placeholders like `{store_id}`

#### Methods

##### pathParam(String key, String value)
Replaces path placeholders with values. Placeholders use curly brace syntax: `{parameter_name}`. Values are automatically URL-encoded.

```java
.pathParam("store_id", "01YCP46JKYM8FJCQ37NMBYHE5X")
.pathParam("model_id", "01G5JAVJ41T49E9TT3SKVS7X1J")
```

##### queryParam(String key, String value)
Adds query parameters to the URL. Parameters are automatically URL-encoded.

```java
.queryParam("page", "1")
.queryParam("limit", "50")
```

##### header(String key, String value)
Adds HTTP headers to the request. Standard headers (Authorization, Content-Type, User-Agent) are managed by the SDK.

```java
.header("X-Request-ID", "unique-id-123")
.header("X-Custom-Header", "value")
```

##### body(Object body)
Sets the request body. Objects and Maps are serialized to JSON. Strings are sent without modification.
.body(new CustomRequest("data", 123))

// Map
.body("{\"raw\":\"json\"}")
```
// POJO
### 2. RawApi

// String

#### Accessing RawApi

```java
OpenFgaClient client = new OpenFgaClient(config);
RawApi rawApi = client.raw();
```

#### Methods

##### send(RawRequestBuilder request)
Execute a request and return the response as a raw JSON string.

```java
CompletableFuture<ApiResponse<String>> future = rawApi.send(request);
```

##### send(RawRequestBuilder request, Class<T> responseType)
Execute a request and deserialize the response into a typed object.

```java
CompletableFuture<ApiResponse<MyResponse>> future = rawApi.send(request, MyResponse.class);
```

### 3. ApiResponse<T>

The response object returned by the Raw API.

```java
public class ApiResponse<T> {
    int getStatusCode()              // HTTP status code
    Map<String, List<String>> getHeaders()  // Response headers
    String getRawResponse()          // Raw JSON response body
    T getData()                      // Deserialized response data
}
```

## Usage Examples

### Example 1: GET Request with Typed Response

```java
// Define your response type
public class FeatureResponse {
    public boolean enabled;
    public String version;
}

// Build and execute request
RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/experimental-feature")
    .pathParam("store_id", client.getStoreId());

client.raw().send(request, FeatureResponse.class)
    .thenAccept(response -> {
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Enabled: " + response.getData().enabled);
        System.out.println("Version: " + response.getData().version);
    });
```

### Example 2: POST Request with Request Body

```java
// Define request and response types
public class BulkDeleteRequest {
    public String olderThan;
    public String type;
    public int limit;
}

public class BulkDeleteResponse {
    public int deletedCount;
    public String message;
}

// Build request with body
BulkDeleteRequest requestBody = new BulkDeleteRequest();
requestBody.olderThan = "2023-01-01";
requestBody.type = "user";
requestBody.limit = 1000;

RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/bulk-delete")
    .pathParam("store_id", client.getStoreId())
    .queryParam("force", "true")
    .body(requestBody);

// Execute
client.raw().send(request, BulkDeleteResponse.class)
    .thenAccept(response -> {
        System.out.println("Deleted: " + response.getData().deletedCount);
    });
```

### Example 3: Working with Raw JSON

```java
RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/complex-data")
    .pathParam("store_id", client.getStoreId());

// Get raw JSON for inspection or custom parsing
client.raw().send(request)
    .thenAccept(response -> {
        String json = response.getRawResponse();
        System.out.println("Raw JSON: " + json);
        // Parse manually if needed
    });
```

### Example 4: Query Parameters and Pagination

```java
RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/items")
    .pathParam("store_id", client.getStoreId())
    .queryParam("page", "1")
    .queryParam("limit", "50")
    .queryParam("filter", "active")
    .queryParam("sort", "created_at");

client.raw().send(request, ItemsResponse.class)
    .thenAccept(response -> {
        // Process paginated results
    });
```

### Example 5: Custom Headers

```java
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/action")
    .pathParam("store_id", client.getStoreId())
    .header("X-Request-ID", UUID.randomUUID().toString())
    .header("X-Client-Version", "1.0.0")
    .header("X-Idempotency-Key", "unique-key-123")
    .body(actionData);

client.raw().send(request, ActionResponse.class)
    .thenAccept(response -> {
        // Handle response
    });
```

### Example 6: Error Handling

```java
RawRequestBuilder request = RawRequestBuilder.builder("DELETE", "/stores/{store_id}/resource/{id}")
    .pathParam("store_id", client.getStoreId())
    .pathParam("id", resourceId);

client.raw().send(request)
    .thenAccept(response -> {
        System.out.println("Successfully deleted. Status: " + response.getStatusCode());
    })
    .exceptionally(e -> {
        // Standard SDK error handling applies:
        if (e.getCause() instanceof FgaError) {
            FgaError error = (FgaError) e.getCause();
            System.err.println("API Error: " + error.getMessage());
            System.err.println("Status Code: " + error.getStatusCode());
        } else {
            System.err.println("Network Error: " + e.getMessage());
        }
        return null;
    });
```

### Example 7: Using Map for Request Body

```java
// Quick prototyping with Map instead of creating a POJO
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/configure")
    .pathParam("store_id", client.getStoreId())
    .body(Map.of(
        "setting", "value",
        "enabled", true,
        "threshold", 100,
        "options", List.of("opt1", "opt2")
    ));

client.raw().send(request, ConfigureResponse.class)
    .thenAccept(response -> {
        System.out.println("Configuration updated");
    });
```

## Best Practices

### 1. Define Response Types

Define POJOs for response structures:

```java
public class ApiResponse {
    @JsonProperty("field_name")
    public String fieldName;
    
    public int count;
}
```

### 2. Handle Errors

Include error handling in production code:

```java
client.raw().send(request, ResponseType.class)
    .thenAccept(response -> {
        // Handle success
    })
    .exceptionally(e -> {
        logger.error("Request failed", e);
        return null;
    });
```

### 3. URL Encoding

The SDK automatically URL-encodes parameters. Do not manually encode:

```java
// Correct
.pathParam("id", "store with spaces")

// Incorrect - double encoding
.pathParam("id", URLEncoder.encode("store with spaces", UTF_8))
```


## Migration Path

When the SDK adds official support for an endpoint you're using via Raw API:

### Before (Raw API)
```java
RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/check")
    .pathParam("store_id", client.getStoreId())
    .body(checkRequest);

client.raw().send(request, CheckResponse.class)
    .thenAccept(response -> {
        // Handle response
    });
```

### After (Typed SDK Method)
```java
client.check(checkRequest)
    .thenAccept(response -> {
        // Handle response - same structure!
    });
```

The response structure remains the same, making migration straightforward.

## Limitations

1. **No Code Generation**: Unlike typed methods, Raw API requests don't benefit from IDE autocomplete for request/response structures
2. **Manual Type Definitions**: You need to define your own POJOs for request/response types
3. **Less Validation**: The SDK can't validate request structure before sending
4. **Documentation**: You'll need to refer to OpenFGA API documentation for endpoint details

## When to Use Raw API

✅ **Use Raw API when:**
- The endpoint is experimental or in beta
- The endpoint was just released and SDK support is pending
- You need to quickly prototype with new features
- You have custom OpenFGA extensions

❌ **Use Typed SDK Methods when:**
- The endpoint has official SDK support
- You want maximum type safety and validation
- You prefer IDE autocomplete and compile-time checks
- The endpoint is stable and well-documented

## Support and Feedback

If you find yourself frequently using the Raw API for a particular endpoint, please:
1. Open an issue on the SDK repository requesting official support
2. Share your use case and the endpoint details
3. Consider contributing a pull request with typed method implementation

The goal of the Raw API is to provide flexibility while we work on comprehensive SDK support for all OpenFGA features.
