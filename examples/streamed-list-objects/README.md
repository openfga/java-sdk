# Streamed List Objects Example

> **NOTE:** This example is temporarily disabled as the `streamedListObjects` API is not yet available for public use. It will be enabled in a future release.

<!--
Demonstrates using `StreamedListObjects` to retrieve objects via the streaming API in the Java SDK.

## What is StreamedListObjects?

The Streamed ListObjects API is very similar to the ListObjects API, with two key differences:

1. **Streaming Results**: Instead of collecting all objects before returning a response, it streams them to the client as they are collected.
2. **No Pagination Limit**: Returns all results without the 1000-object limit of the standard ListObjects API.

This makes it ideal for scenarios where you need to retrieve large numbers of objects, especially when querying computed relations.

## Prerequisites

- Java 11 or higher
- OpenFGA server running on `http://localhost:8080` (or set `FGA_API_URL`)

## Running

```bash
# From the SDK root directory, build the SDK first
./gradlew build

# Then run the example
cd examples/streamed-list-objects
./gradlew run
```

Or using the Makefile:

```bash
make build
make run
```

## What it does

- Creates a temporary store
- Writes an authorization model with **computed relations**
- Adds 2000 tuples (1000 owners + 1000 viewers)
- Queries the **computed `can_read` relation** via `StreamedListObjects`
- Shows all 2000 results (demonstrating computed relations)
- Shows progress (first 3 objects and every 500th)
- Cleans up the store

## Authorization Model

The example demonstrates OpenFGA's **computed relations**:

```
type user

type document
  relations
    define owner: [user]
    define viewer: [user]
    define can_read: owner or viewer
```

**Why this matters:**
- We write tuples to `owner` and `viewer` (base permissions)
- We query `can_read` (computed from owner OR viewer)

**Example flow:**
1. Write: `user:anne owner document:1-1000`
2. Write: `user:anne viewer document:1001-2000`
3. Query: `StreamedListObjects(user:anne, relation:can_read, type:document)`
4. Result: All 2000 documents (because `can_read = owner OR viewer`)

## Key Features Demonstrated

### CompletableFuture-based Streaming Pattern

The `streamedListObjects` method uses Java's `CompletableFuture` with a consumer callback to handle streaming data:

```java
fga.streamedListObjects(request, response -> {
    System.out.println("Received: " + response.getObject());
}).get(); // Wait for completion
```

### Early Break and Cleanup

The streaming implementation properly handles early termination through cancellation:

```java
AtomicBoolean shouldStop = new AtomicBoolean(false);
CompletableFuture<Void> future = fga.streamedListObjects(request, response -> {
    System.out.println(response.getObject());
    if (someCondition) {
        shouldStop.set(true);
    }
});

// Cancel if needed
if (shouldStop.get()) {
    future.cancel(true);
}
```

### Exception Handling

The example demonstrates proper error handling:

```java
try {
    fga.streamedListObjects(request, response -> {
        System.out.println(response.getObject());
    }).get();
} catch (ExecutionException ex) {
    if (ex.getCause() instanceof FgaInvalidParameterException) {
        System.err.println("Validation error");
    }
} catch (CancellationException ex) {
    System.err.println("Operation cancelled");
}
```

## Benefits Over ListObjects

- **No Pagination**: Retrieve all objects in a single streaming request
- **Lower Memory**: Objects are processed as they arrive, not held in memory
- **Early Termination**: Can stop streaming at any point without wasting resources
- **Better for Large Results**: Ideal when expecting hundreds or thousands of objects

## Performance Considerations

- Streaming starts immediately - no need to wait for all results
- HTTP connection remains open during streaming
- Properly handles cleanup if consumer stops early
- Supports all the same options as `ListObjects` (consistency, contextual tuples, etc.)
