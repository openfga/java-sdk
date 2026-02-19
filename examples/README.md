## Examples of using the OpenFGA Java SDK

A collection of examples demonstrating how to use the OpenFGA Java SDK in different scenarios.

### Available Examples

#### Basic Examples (`basic-examples/`)
A simple example that creates a store, runs a set of calls against it including creating a model, writing tuples and checking for access. This example is implemented in both Java and Kotlin.

#### OpenTelemetry Examples
- `opentelemetry/` - Demonstrates OpenTelemetry integration both via manual code configuration, as well as no-code instrumentation using the OpenTelemetry java agent

#### Streaming Examples
- `streamed-list-objects/` - Demonstrates using the StreamedListObjects API to retrieve large result sets without pagination limits

#### API Executor Examples (`api-executor/`)
Demonstrates direct HTTP access to OpenFGA endpoints not yet wrapped by the SDK, maintaining full SDK configuration (authentication, retries, error handling, telemetry).

- **`ApiExecutorExample.java`** — standard (non-streaming) endpoints: typed responses, raw JSON, query parameters, custom headers, error handling
- **`StreamingApiExecutorExample.java`** — streaming endpoints: calls `streamed-list-objects` via `client.streamingApiExecutor(typeRef).stream(request, consumer)`, demonstrating per-object callbacks and error handling

Run with `make run` (standard) or `make run-streaming` (streaming) from the `api-executor/` directory.
