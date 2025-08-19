# OpenTelemetry Example for OpenFGA Java SDK

This example demonstrates two approaches for using OpenTelemetry metrics with the OpenFGA Java SDK:

1. **Manual Configuration** (`./gradlew run`) - Code-based OpenTelemetry setup
2. **Java Agent** (`./gradlew runWithAgent`) - Zero-code automatic instrumentation

Both approaches generate the same metrics:
- `fga-client.request.duration` - Total request time for FGA requests
- `fga-client.query.duration` - Time taken by FGA server to process requests
- `fga-client.credentials.request` - Number of token requests (if using client credentials)

## SDK Version Configuration

**By default**, this example uses a published version of the OpenFGA Java SDK. 

If you're contributing to the SDK or testing unreleased features:

1. **Enable local SDK** in `settings.gradle`:
   ```gradle
   // Uncomment this line:
   includeBuild '../..'
   ```

2. **Update dependency** in `build.gradle`:
   ```gradle
   // Comment out the versioned dependency:
   // implementation("dev.openfga:openfga-sdk:$fgaSdkVersion")
   
   // Uncomment the local dependency:
   implementation("dev.openfga:openfga-sdk")
   ```

3. **Build the main SDK first** (from repository root):
   ```bash
   cd ../..
   ./gradlew build
   cd examples/opentelemetry
   ```

## Prerequisites

- Java 11 or higher
- Docker and Docker Compose
- OpenFGA server running (or use the provided docker-compose setup)

## Quick Start

### 1. Start the OpenTelemetry Stack

```bash
# Clone the OpenTelemetry Collector setup
git clone https://github.com/ewanharris/opentelemetry-collector-dev-setup.git otel-collector
cd otel-collector

# Start the services
docker-compose up -d
```

This provides:
- **Jaeger** at http://localhost:16686 - Distributed tracing UI
- **Prometheus** at http://localhost:9090 - Metrics collection and querying  
- **Grafana** at http://localhost:3001 - Metrics visualization (admin:admin)

### 2. Configure OpenFGA Connection

Copy and edit the environment file:
```bash
cp .env.example .env
# Edit .env with your OpenFGA store details
```

### 3. Choose Your Approach

#### Option A: Manual Configuration (./gradlew run)
```bash
./gradlew run
```

**Pros:**
- Full control over OpenTelemetry configuration
- Can customize metrics, exporters, and resources in code
- No external dependencies beyond your application

**Cons:**
- Requires OpenTelemetry SDK dependencies in your application
- More code to write and maintain

#### Option B: Java Agent (./gradlew runWithAgent)  
```bash
./gradlew runWithAgent
```

**Pros:**
- Zero code changes required - completely automatic
- No OpenTelemetry dependencies needed in your application
- Easy to enable/disable by adding/removing the agent

**Cons:**
- Less control over configuration
- Requires downloading and managing the agent JAR

## Viewing Metrics

Both approaches export metrics to the same OTLP endpoint. View them in:

- **Prometheus**: http://localhost:9090/graph
  - Query: `fga_client_request_duration_bucket`
  - Query: `fga_client_query_duration_bucket`  
  - Query: `fga_client_credentials_request_total`

- **Grafana**: http://localhost:3001 (admin:admin)
  - Import dashboard from `grafana/` directory
  - Or create custom dashboards with the FGA metrics

## Architecture

### Manual Configuration Mode
```
Your App → OpenTelemetry SDK → OTLP Exporter → Collector → Prometheus/Jaeger
```

The application code:
1. Configures OpenTelemetry SDK with OTLP exporter
2. Creates OpenFGA client with default telemetry enabled
3. Performs FGA operations which generate metrics
4. Metrics are exported to the OTLP collector

### Java Agent Mode  
```
Your App → OpenTelemetry Agent → OTLP Exporter → Collector → Prometheus/Jaeger
```

The OpenTelemetry agent:
1. Automatically detects and instruments the OpenFGA SDK
2. Configures exporters based on system properties
3. Collects metrics without any code changes
4. Exports to the same OTLP collector

## Troubleshooting

### No Metrics Appearing
1. Verify OTLP collector is running on localhost:4317
2. Check the application logs for OpenTelemetry initialization messages
3. Ensure FGA operations are actually being performed

### Manual Configuration Issues
- Verify all OpenTelemetry dependencies are included
- Check that `buildAndRegisterGlobal()` is called before creating the FGA client

### Java Agent Issues  
- Verify the agent JAR was downloaded successfully
- Check that OTEL system properties are set correctly
- Ensure the agent is being loaded (look for agent startup messages)

### Connection Issues
- Verify your `.env` file has correct FGA_STORE_ID and FGA_MODEL_ID
- Check that your OpenFGA server is accessible
- Verify authentication credentials if using a protected OpenFGA instance

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

## Next Steps

- Explore the metrics in Grafana with custom dashboards
- Try different telemetry configurations to see what works best for your use case
- Consider which approach (manual vs agent) fits better with your deployment strategy

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
