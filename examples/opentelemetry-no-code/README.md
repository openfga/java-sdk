# OpenFGA Java SDK - No-Code OpenTelemetry Example

This example demonstrates how to use OpenTelemetry metrics with the OpenFGA Java SDK using the **no-code approach** with OpenTelemetry Java agents.

## Key Benefits

- **No OpenTelemetry configuration code** needed in your application
- **Automatic instrumentation** of the OpenFGA SDK and other libraries
- **Zero code changes** required to enable metrics
- **Production-ready** approach used by many organizations

## Important Note

✅ **This example now uses the local SDK build with the OpenTelemetry bug fix!**

The SDK has been fixed to use `GlobalOpenTelemetry.get()` instead of `OpenTelemetry.noop()`, enabling proper metrics export. This example demonstrates the no-code approach with the fixed SDK, performing real OpenFGA operations that generate actual metrics.

## How It Works

The OpenTelemetry Java agent automatically:
1. Configures OpenTelemetry and registers it globally
2. Instruments supported libraries (including the OpenFGA SDK)
3. Exports metrics to your configured backend

The OpenFGA SDK detects the agent-configured OpenTelemetry instance via `GlobalOpenTelemetry.get()` and automatically starts generating metrics.

## Prerequisites

1. **OpenTelemetry Collector** running (see [collector setup](https://github.com/ewanharris/opentelemetry-collector-dev-setup))
2. **OpenTelemetry Java Agent** downloaded
3. **OpenFGA store and authorization model** configured

## Setup

### 1. Download OpenTelemetry Java Agent

```bash
# Download the latest OpenTelemetry Java agent
curl -L -o opentelemetry-javaagent.jar \
  https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

### 2. Configure Environment

Copy `.env.example` to `.env` and configure your OpenFGA settings:

```bash
cp .env.example .env
# Edit .env with your store ID, model ID, and authentication details
```

### 3. Start OpenTelemetry Collector

```bash
# Clone and start the collector setup
git clone https://github.com/ewanharris/opentelemetry-collector-dev-setup.git
cd opentelemetry-collector-dev-setup
docker-compose up -d
```

## Running the Example

### Option 1: Using Gradle Task (Recommended)

```bash
# Run with OpenTelemetry agent
./gradlew runWithAgent
```

### Option 2: Manual Java Command

```bash
# Build the application
./gradlew build

# Run with OpenTelemetry agent
java -javaagent:opentelemetry-javaagent.jar \
     -Dotel.service.name=openfga-java-sdk-no-code-example \
     -Dotel.service.version=1.0.0 \
     -Dotel.exporter.otlp.endpoint=http://localhost:4318 \
     -Dotel.exporter.otlp.protocol=http/protobuf \
     -jar build/libs/openfga-sdk-opentelemetry-no-code-example.jar
```

### Option 3: Using Environment Variables

```bash
# Set OpenTelemetry configuration via environment variables
export OTEL_SERVICE_NAME=openfga-java-sdk-no-code-example
export OTEL_SERVICE_VERSION=1.0.0
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf

# Run with agent
java -javaagent:opentelemetry-javaagent.jar -jar build/libs/your-app.jar
```

## Configuration Options

The OpenTelemetry Java agent can be configured via:

### JVM System Properties
```bash
-Dotel.service.name=your-service-name
-Dotel.service.version=1.0.0
-Dotel.exporter.otlp.endpoint=http://localhost:4317
-Dotel.exporter.otlp.headers=api-key=your-api-key
```

### Environment Variables
```bash
OTEL_SERVICE_NAME=your-service-name
OTEL_SERVICE_VERSION=1.0.0
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_HEADERS=api-key=your-api-key
```

### Configuration File
Create `otel.properties`:
```properties
otel.service.name=your-service-name
otel.service.version=1.0.0
otel.exporter.otlp.endpoint=http://localhost:4317
```

## Generated Metrics

The example will generate the following OpenFGA SDK metrics:

| Metric Name | Type | Description |
|-------------|------|-------------|
| `fga-client.request.duration` | Histogram | Total request time for FGA requests |
| `fga-client.query.duration` | Histogram | Time taken by FGA server to process requests |
| `fga-client.credentials.request` | Counter | Number of token requests (if using auth) |

## Viewing Metrics

Once the example runs, check your observability tools:

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin:admin)
- **Jaeger**: http://localhost:16686

## Comparison with Manual Configuration

| Approach | Code Changes | Configuration | Flexibility |
|----------|--------------|---------------|-------------|
| **No-Code (Agent)** | None | External (JVM args/env vars) | High |
| **Manual Configuration** | Required | In application code | Very High |

## Troubleshooting

### Agent Not Working
1. Verify agent JAR path is correct
2. Check JVM arguments are properly set
3. Ensure collector is running and accessible

### No Metrics Appearing
1. Verify OpenTelemetry collector is running
2. Check agent configuration (service name, endpoint)
3. Wait 1-2 minutes for metrics export
4. Check collector logs for errors

### Agent Configuration Issues
```bash
# Enable agent debug logging
-Dotel.javaagent.debug=true

# Enable OpenTelemetry SDK debug logging
-Dotel.java.global-autoconfigure.enabled=true
```

## Production Deployment

For production use:

1. **Use environment variables** instead of JVM system properties
2. **Configure proper service names** and versions
3. **Set up secure OTLP endpoints** with authentication
4. **Monitor agent overhead** (typically <5% CPU/memory)
5. **Use agent configuration files** for complex setups

## AWS OpenTelemetry Agent

For AWS environments, you can use the AWS Distro for OpenTelemetry:

```bash
# Download AWS OpenTelemetry Java agent
curl -L -o aws-opentelemetry-agent.jar \
  https://github.com/aws-observability/aws-otel-java-instrumentation/releases/latest/download/aws-opentelemetry-agent.jar

# Run with AWS agent
java -javaagent:aws-opentelemetry-agent.jar \
     -Dotel.exporter.otlp.endpoint=https://your-aws-collector-endpoint \
     -jar your-app.jar
```

## Next Steps

- Explore [manual OpenTelemetry configuration](../opentelemetry/) for more control
- Set up custom metrics and traces in your application
- Configure alerting based on OpenFGA metrics
- Integrate with your organization's observability stack
