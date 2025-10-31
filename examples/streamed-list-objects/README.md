# Streamed List Objects Example

This example demonstrates how to use the Streamed ListObjects API in the OpenFGA Java SDK.

## What is Streamed ListObjects?

The StreamedListObjects API is similar to the regular ListObjects API, but with key differences:

1. **Streaming Response**: Instead of collecting all objects before returning a response, it streams them to the client as they are collected
2. **No Result Limit**: The number of results returned is only limited by the execution timeout specified in the server configuration (`OPENFGA_LIST_OBJECTS_DEADLINE`)
3. **Immediate Processing**: You can start processing results as they arrive, without waiting for the entire result set

## When to Use Streamed ListObjects

Use the Streamed ListObjects API when:

- You expect a large number of results that would take a long time to collect
- You want to start processing results immediately rather than waiting for the complete set
- You might not need all results (e.g., you want to stop after finding a certain number)
- You want to avoid timeout issues with very large result sets

## Running the Example

### Prerequisites

- A running OpenFGA server (or use the [OpenFGA Playground](https://play.fga.dev/))

### Environment Variables

Set the following environment variables:

```bash
# Required
export FGA_API_URL=http://localhost:8080  # Your OpenFGA server URL

# Optional - for authenticated servers
export FGA_CLIENT_ID=your_client_id
export FGA_CLIENT_SECRET=your_client_secret
export FGA_API_TOKEN_ISSUER=your_token_issuer
export FGA_API_AUDIENCE=your_audience
```

### Running

```bash
make run
```

## Code Examples

### Basic Usage

```java
// Create a request
var request = new ClientListObjectsRequest()
    .type("document")
    .relation("owner")
    .user("user:anne");

// Call the streaming API
var objectStream = fgaClient.streamedListObjects(request).get();

// Collect all results
List<String> objects = objectStream
    .map(StreamedListObjectsResponse::getObject)
    .collect(Collectors.toList());
```

### Early Termination

```java
// Get only the first 10 results
var objectStream = fgaClient.streamedListObjects(request).get();
List<String> firstTen = objectStream
    .map(StreamedListObjectsResponse::getObject)
    .limit(10)
    .collect(Collectors.toList());
```

### Process as You Go

```java
// Process each object immediately as it arrives
var objectStream = fgaClient.streamedListObjects(request).get();
objectStream
    .map(StreamedListObjectsResponse::getObject)
    .forEach(obj -> {
        // Do something with each object
        System.out.println("Processing: " + obj);
    });
```

### With Options

```java
// Use options to specify consistency preference
var options = new ClientListObjectsOptions()
    .consistency(ConsistencyPreference.HIGHER_CONSISTENCY)
    .authorizationModelId("01GXSXXXXXXXXXXXXXXXX");

var objectStream = fgaClient.streamedListObjects(request, options).get();
```

## Comparison with Regular ListObjects

| Feature | ListObjects | Streamed ListObjects |
|---------|-------------|---------------------|
| Result Collection | Waits for all results | Streams results as computed |
| Result Limit | Limited by server pagination | Limited only by execution timeout |
| Processing | Must wait for complete response | Can process immediately |
| Use Case | Small to medium result sets | Large result sets, immediate processing |
