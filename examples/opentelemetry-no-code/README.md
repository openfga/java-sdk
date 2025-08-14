# OpenTelemetry No-Code Example

This example demonstrates how to use OpenTelemetry with the OpenFGA Java SDK using the **no-code approach** with the OpenTelemetry Java agent.

## Prerequisites

- Java 11 or higher
- OpenFGA server running with existing store and authorization model
- OpenTelemetry Java agent JAR file

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

### 4. Download OpenTelemetry Java Agent

```bash
wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

### 5. Configure Environment Variables

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

### Using the runWithAgent task (Recommended)
```bash
./gradlew runWithAgent
```

### Using Gradle directly (if runWithAgent doesn't work)
```bash
gradle run -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=openfga-java-sdk-no-code-example \
  -Dotel.service.version=1.0.0 \
  -Dotel.exporter.otlp.endpoint=http://localhost:4317 \
  -Dotel.exporter.otlp.protocol=grpc
```

### If Gradle wrapper needs regeneration
```bash
gradle wrapper --gradle-version 8.5
./gradlew runWithAgent
```

## What It Does

The example runs continuously, performing these operations every 5 seconds:

1. **Read authorization model** - Gets the existing authorization model
2. **Read existing tuples** - Reads all tuples from the store  
3. **Check operations** - Tests specific user permissions:
   - `user:anne` can `viewer` `document:2021-budget`
   - `user:beth` can `writer` `document:2021-budget` 
   - `user:anne` can `viewer` `document:2022-budget`
4. **Batch check** - Same checks but in a single batch request
5. **List objects** - Lists documents that `user:anne` can view

## Viewing Metrics

Metrics will be sent to the OpenTelemetry Collector at http://localhost:4317 and then forwarded to Prometheus at http://localhost:9090/metrics

In Grafana (http://localhost:3001), look for metrics prefixed with `fga_client_`:
- `fga_client_request_duration_seconds` - Total request duration
- `fga_client_query_duration_seconds` - Server processing time  
- `fga_client_credentials_request_total` - Authentication requests

## How It Works

- **No configuration code needed** - The Java agent handles all OpenTelemetry setup
- **Automatic instrumentation** - Agent detects and configures OpenTelemetry globally
- **SDK metrics enabled** - The FGA SDK detects the global OpenTelemetry instance
