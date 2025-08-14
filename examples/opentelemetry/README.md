# OpenTelemetry Example for OpenFGA Java SDK

This example demonstrates how to configure and use OpenTelemetry metrics with the OpenFGA Java SDK. It shows both default and custom telemetry configurations and performs various OpenFGA operations to generate metrics that can be observed through Prometheus, Grafana, and Jaeger.

## Features Demonstrated

- **OpenTelemetry SDK Configuration**: Manual configuration with OTLP gRPC exporter
- **Default Telemetry**: Using the SDK's built-in telemetry configuration
- **Custom Telemetry**: Configuring specific metrics and attributes
- **Metrics Generation**: Various OpenFGA operations that generate the three supported metrics:
  - `fga-client.request.duration` - Total request time for FGA requests
  - `fga-client.query.duration` - Time taken by FGA server to process requests
  - `fga-client.credentials.request` - Number of token requests (if using client credentials)

## Prerequisites

- Java 11 or higher
- Docker and Docker Compose
- OpenFGA server running (or use the provided docker-compose setup)

## Setup

### 1. Clone the OpenTelemetry Collector Dev Setup

```bash
git clone https://github.com/ewanharris/opentelemetry-collector-dev-setup.git otel-collector
cd otel-collector
```

### 2. Start the OpenTelemetry Stack

```bash
docker-compose up -d
```

This will start:
- **Jaeger** at http://localhost:16686 - Distributed tracing UI
- **Zipkin** at http://localhost:9411 - Alternative tracing UI  
- **Prometheus** at http://localhost:9090 - Metrics collection and querying
- **Grafana** at http://localhost:3001 - Metrics visualization (admin:admin)

### 3. Start OpenFGA Server (if not already running)

You can use Docker to run OpenFGA locally:

```bash
docker run -p 8080:8080 openfga/openfga run
```

Or follow the [OpenFGA installation guide](https://openfga.dev/docs/getting-started/setup-openfga).

### 4. Configure Environment Variables

Copy the example environment file and configure it:

```bash
cp .env.example .env
```

### Step 1: Create Store and Authorization Model

You need to create a store and authorization model in your OpenFGA instance:

1. **Create a store** using the OpenFGA CLI, API, or dashboard
2. **Create an authorization model** using the JSON structure provided in `authorization-model.json`:
   ```bash
   # Using OpenFGA CLI (if you have it installed)
   fga store create --name "OpenTelemetry Example Store"
   fga model write --file authorization-model.json
   ```
   
   Or use the OpenFGA API directly to create the store and model.

3. **Note the Store ID and Authorization Model ID** from the responses

### Step 2: Configure Environment Variables

Edit `.env` with your OpenFGA server details and the IDs from Step 1:

```bash
# OpenFGA Configuration
FGA_API_URL=https://api.us1.fga.dev  # Or your OpenFGA server URL
FGA_STORE_ID=01ARZ3NDEKTSV4RRFFQ69G5FAV  # Your actual store ID
FGA_MODEL_ID=01ARZ3NDEKTSV4RRFFQ69G5FAV  # Your actual authorization model ID

# OpenFGA Authentication (required for hosted service)
FGA_CLIENT_ID=your_client_id
FGA_CLIENT_SECRET=your_client_secret

# OpenTelemetry Configuration
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
OTEL_SERVICE_NAME=openfga-java-sdk-example
OTEL_SERVICE_VERSION=1.0.0
```

### Demo Mode (No Store Required)

If you don't want to create a store, you can run the demo in configuration-only mode by leaving `FGA_STORE_ID` empty. The example will show telemetry configuration without performing actual operations:

```bash
./gradlew runDemo  # Shows telemetry configuration only
```

## Running the Example

### From the examples/opentelemetry directory:

```bash
# Build and run the example
./gradlew run
```

### From the root SDK directory:

```bash
# Build and run the opentelemetry example
./gradlew :examples:opentelemetry:run
```

## What the Example Does

The example performs the following operations to generate telemetry data:

1. **Configures OpenTelemetry** with OTLP gRPC exporter pointing to the local collector
2. **Creates OpenFGA client** with default telemetry configuration
3. **Sets up test environment**:
   - Creates a new store
   - Creates an authorization model with `user` and `document` types
4. **Performs various operations** with default telemetry:
   - Writes relationship tuples
   - Performs check operations
   - Lists objects
   - Reads tuples
5. **Recreates client** with custom telemetry configuration (fewer attributes)
6. **Performs additional operations** with custom telemetry:
   - Batch check operations
   - Lists users
   - Writes and deletes tuples

## Observing Metrics

### Prometheus (http://localhost:9090)

Query for OpenFGA metrics:
- `fga_client_request_duration_bucket` - Request duration histogram
- `fga_client_query_duration_bucket` - Query duration histogram  
- `fga_client_credentials_request_total` - Credentials request counter

Example queries:
```promql
# Average request duration by method
rate(fga_client_request_duration_sum[5m]) / rate(fga_client_request_duration_count[5m])

# Request rate by HTTP status code
rate(fga_client_request_duration_count[5m])

# 95th percentile request duration
histogram_quantile(0.95, rate(fga_client_request_duration_bucket[5m]))
```

### Grafana (http://localhost:3001)

Login with `admin:admin`. The collector setup includes pre-configured dashboards for OpenFGA metrics.

### Jaeger (http://localhost:16686)

While this example focuses on metrics, you can also observe any traces if tracing is configured.

## Supported Metrics and Attributes

### Metrics

| Metric Name | Type | Description |
|-------------|------|-------------|
| `fga-client.request.duration` | Histogram | Total request time for FGA requests, in milliseconds |
| `fga-client.query.duration` | Histogram | Time taken by the FGA server to process and evaluate the request, in milliseconds |
| `fga-client.credentials.request` | Counter | Total number of new token requests initiated using the Client Credentials flow |

### Attributes

| Attribute Name | Type | Description |
|----------------|------|-------------|
| `fga-client.request.client_id` | string | Client ID associated with the request, if any |
| `fga-client.request.method` | string | FGA method/action that was performed (e.g., Check, ListObjects) |
| `fga-client.request.model_id` | string | Authorization model ID that was sent as part of the request, if any |
| `fga-client.request.store_id` | string | Store ID that was sent as part of the request |
| `fga-client.response.model_id` | string | Authorization model ID that the FGA server used |
| `http.host` | string | Host identifier of the origin the request was sent to |
| `http.request.method` | string | HTTP method for the request |
| `http.request.resend_count` | int | Number of retries attempted, if any |
| `http.response.status_code` | int | Status code of the response (e.g., `200` for success) |
| `url.scheme` | string | HTTP scheme of the request (`http`/`https`) |
| `url.full` | string | Full URL of the request |
| `user_agent.original` | string | User Agent used in the query |

## Telemetry Configuration

### Default Configuration

The SDK includes a default telemetry configuration that enables all metrics with all default attributes:

```java
// Uses default telemetry configuration
OpenFgaClient client = new OpenFgaClient(new ClientConfiguration()
    .apiUrl("http://localhost:8080"));
```

### Custom Configuration

You can customize which metrics and attributes are collected:

```java
// Create custom telemetry configuration
Map<Attribute, Optional<Object>> customAttributes = new HashMap<>();
customAttributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, Optional.empty());
customAttributes.put(Attributes.FGA_CLIENT_REQUEST_STORE_ID, Optional.empty());
customAttributes.put(Attributes.HTTP_RESPONSE_STATUS_CODE, Optional.empty());

Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();
metrics.put(Histograms.REQUEST_DURATION, customAttributes);
metrics.put(Histograms.QUERY_DURATION, customAttributes);

TelemetryConfiguration telemetryConfig = new TelemetryConfiguration(metrics);

OpenFgaClient client = new OpenFgaClient(new ClientConfiguration()
    .apiUrl("http://localhost:8080")
    .telemetryConfiguration(telemetryConfig));
```

## Troubleshooting

### No Metrics Appearing

1. **Check OpenTelemetry collector is running**:
   ```bash
   docker-compose ps
   ```

2. **Verify OTLP endpoint is correct** in your `.env` file:
   ```bash
   OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
   ```

3. **Check collector logs**:
   ```bash
   docker-compose logs otel-collector
   ```

### Connection Issues

1. **Ensure OpenFGA server is running** on the configured port
2. **Check firewall settings** - ensure ports 4317 (OTLP), 9090 (Prometheus), 3001 (Grafana) are accessible
3. **Verify network connectivity** between the example application and the collector

### Authentication Issues

If using client credentials authentication:
1. **Verify client ID and secret** are correct
2. **Check the token issuer URL** matches your OpenFGA server
3. **Ensure the API audience** is configured correctly

## Cleanup

To stop the OpenTelemetry stack:

```bash
cd otel-collector
docker-compose down
```

## Learn More

- [OpenFGA Documentation](https://openfga.dev/docs)
- [OpenFGA Java SDK Documentation](../../README.md)
- [OpenTelemetry Java Documentation](https://opentelemetry.io/docs/languages/java/)
- [OpenFGA Telemetry Documentation](../../docs/OpenTelemetry.md)

# OpenTelemetry Manual Configuration Example

This example demonstrates how to use OpenTelemetry with the OpenFGA Java SDK using **manual configuration** with code-based setup.

## Prerequisites

- Java 11 or higher
- OpenFGA server running with existing store and authorization model

## Setup

### Configure Environment Variables

Copy `.env.example` to `.env` and update with your values:

```bash
cp .env.example .env
# Edit .env with your actual FGA store ID, model ID, and credentials
```

The `.env` file should contain:

```bash
# FGA Configuration (REQUIRED)
FGA_API_URL=http://localhost:8080
FGA_STORE_ID=your-actual-store-id
FGA_MODEL_ID=your-actual-model-id

# Authentication (optional)
FGA_CLIENT_ID=your-client-id
FGA_CLIENT_SECRET=your-client-secret
FGA_API_AUDIENCE=https://api.fga.example
FGA_API_TOKEN_ISSUER=auth.fga.example
```

## Running the Example

### Option 1: Using Gradle Wrapper (if available)
```bash
./gradlew run
```

### Option 2: Using Gradle directly
```bash
gradle run
```

### Option 3: Regenerate Gradle Wrapper (if needed)
```bash
gradle wrapper --gradle-version 8.5
./gradlew run
```

## What It Does

The example runs continuously, performing these operations every 5 seconds:

1. **Manual OpenTelemetry setup** - Configures OpenTelemetry SDK with Prometheus exporter
2. **Read authorization model** - Gets the existing authorization model
3. **Read existing tuples** - Reads all tuples from the store  
4. **Check operations** - Tests specific user permissions:
   - `user:anne` can `viewer` `document:2021-budget`
   - `user:beth` can `writer` `document:2021-budget` 
   - `user:anne` can `viewer` `document:2022-budget`
5. **Batch check** - Same checks but in a single batch request
6. **List objects** - Lists documents that `user:anne` can view

## Viewing Metrics

The example starts a Prometheus metrics server on port 9090. Metrics will be available at:
http://localhost:9090/metrics

Look for metrics prefixed with `fga_client_`:
- `fga_client_request_duration_seconds` - Total request duration
- `fga_client_query_duration_seconds` - Server processing time  
- `fga_client_credentials_request_total` - Authentication requests

## How It Works

- **Code-based configuration** - OpenTelemetry is configured programmatically in the application
- **Prometheus exporter** - Metrics are exported via HTTP server on port 9090
- **SDK metrics enabled** - The FGA SDK uses the configured OpenTelemetry instance

## Comparison with No-Code Example

- **Manual**: Requires explicit OpenTelemetry configuration in code, full control
- **No-Code**: Uses Java agent, no configuration code needed
- **Both**: Same FGA operations, same metrics generated, same authentication support
