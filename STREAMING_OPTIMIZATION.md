# Optimization: Remove Unnecessary Thread Pool Hop in Streaming

## Issue Raised
The original concern was that `CompletableFuture.runAsync()` uses `ForkJoinPool.commonPool()` which may not be appropriate for I/O-bound operations like processing streaming responses.

## Analysis

### Original Code
```java
.thenCompose(response -> {
    // Check status...
    
    // Process the stream asynchronously on a separate thread
    return CompletableFuture.runAsync(() -> {
        try (Stream<String> lines = response.body()) {
            lines.forEach(line -> {
                processLine(line, consumer, errorConsumer);
            });
        }
    });
})
```

### Issues with Original Approach
1. **Unnecessary thread hop**: The `thenCompose` already runs on an appropriate executor thread from HttpClient
2. **Extra complexity**: Adding `runAsync` and `thenCompose` when `thenApply` would suffice
3. **Potential confusion**: Suggests I/O-bound work when it's actually CPU-bound (JSON parsing)

### Reality Check
- The `response.body()` returns a `Stream<String>` that's **already buffered** by HttpClient
- We're **not doing blocking I/O** - just iterating over in-memory lines
- The work is **CPU-bound** (JSON parsing with Jackson), not I/O-bound
- `ForkJoinPool.commonPool()` is actually reasonable for CPU work, BUT...

## Solution: Use thenApply Instead

### Optimized Code
```java
.thenApply(response -> {
    // Check status...
    
    // Process the stream - runs on HttpClient's executor thread
    try (Stream<String> lines = response.body()) {
        lines.forEach(line -> {
            processLine(line, consumer, errorConsumer);
        });
    }
    return (Void) null;
})
```

### Benefits
1. ✅ **No unnecessary thread hop** - processes directly on HttpClient's executor
2. ✅ **Simpler code** - uses `thenApply` instead of `thenCompose` + `runAsync`
3. ✅ **More efficient** - one less context switch
4. ✅ **Consistent with codebase** - follows patterns used elsewhere in the SDK
5. ✅ **Same executor context** - uses the executor configured for the HttpClient

## Thread Pool Context

The processing now runs on:
- **HttpClient's executor** if one was explicitly configured via `HttpClient.Builder.executor()`
- **HttpClient's default executor** otherwise (which is suitable for this work)

This is better than using `ForkJoinPool.commonPool()` because:
- It respects any custom executor configuration
- It keeps the work in the same execution context as the HTTP operation
- It's simpler and more efficient

## Verification

- ✅ All existing tests pass
- ✅ Build succeeds
- ✅ Example project works correctly
- ✅ No functional changes, only optimization

## Conclusion

**The concern was valid** - we were using an unnecessary `runAsync` that added complexity and a thread hop. However, **the real issue wasn't about ForkJoinPool vs I/O executor**, but rather that we didn't need `runAsync` at all.

The fix is simpler and more efficient: just use `thenApply` and process the stream directly on the HttpClient's executor thread.

