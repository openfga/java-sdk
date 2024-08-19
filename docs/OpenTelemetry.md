# OpenTelemetry

This SDK produces [metrics](https://opentelemetry.io/docs/concepts/signals/metrics/) using [OpenTelemetry](https://opentelemetry.io/) that allow you to view data such as request timings. These metrics also include attributes for the model and store ID, as well as the API called to allow you to build reporting.

When an OpenTelemetry SDK instance is configured, the metrics will be exported and sent to the collector configured as part of your applications configuration. If you are not using OpenTelemetry, the metric functionality is a no-op and the events are never sent.

In cases when metrics events are sent, they will not be viewable outside of infrastructure configured in your application, and are never available to the OpenFGA team or contributors.

## Metrics

### Supported Metrics

| Metric Name                       | Type      | Description                                                                          |
| --------------------------------- | --------- | ------------------------------------------------------------------------------------ |
| `fga-client.credentials.request`  | Counter   | The total number of times a new token was requested when using ClientCredentials     |
| `fga-client.query.duration`       | Histogram | The amount of time the FGA server took to internally process nd evaluate the request |
| `fga-client.request.duration`     | Histogram | The total request time for FGA requests                                              |

### Supported attributes

| Attribute Name                 | Type     | Description                                                                                                                                                 |
| ------------------------------ | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `fga-client.request.client_id` | `string` | The client ID associated with the request, if any                                                                                                           |
| `fga-client.request.method`    | `string` | The FGA method/action that was performed (e.g. `Check`, `ListObjects`, ...) in TitleCase                                                                    |
| `fga-client.request.model_id`  | `string` | The authorization model ID that was sent as part of the request, if any                                                                                     |
| `fga-client.request.store_id`  | `string` | The store ID that was sent as part of the request                                                                                                           |
| `fga-client.response.model_id` | `string` | The authorization model ID that the FGA server used                                                                                                         |
| `fga-client.user`              | `string` | The user that is associated with the action of the request for check and list objects                                                                       |
| `http.host`                    | `string` | Host identifier of the origin the request was sent to                                                                                                       |
| `http.request.method`          | `string` | The HTTP method for the request                                                                                                                             |
| `http.request.resend_count`    | `int`    | The number of retries attempted (Only sent if the request was retried. Count of `1` means the request was retried once in addition to the original request) |
| `http.response.status_code`    | `int`    | The status code of the response                                                                                                                             |
| `url.full`                     | `string` | Full URL of the request                                                                                                                                     |
| `url.scheme`                   | `string` | HTTP Scheme of the request (`http`/`https`)                                                                                                                 |
| `user_agent.original`          | `string` | User Agent used in the query                                                                                                                                |

## Examples

### Usage

Please see [the OpenTelemetry documentation](https://opentelemetry.io/docs/languages/java/) for how to configure the SDK for your application. Below is an example of how to configure the SDK to use OpenTelemetry.

In most cases you can use the `io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk` package to automatically configure OpenTelemetry.

However, if you prefer to configure OpenTelemetry manually, you can use the `io.opentelemetry.sdk.OpenTelemetrySdk` package to configure OpenTelemetry manually, as shown below.

```java
// Import the OpenFGA SDK
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;

// Import the OpenTelemetry SDK
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;

public class Example {
    private OpenTelemetry otel;

    public static void main(String[] args) throws Exception {
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(System.getenv("FGA_API_URL"))
                .storeId(System.getenv("FGA_STORE_ID"))
                .authorizationModelId(System.getenv("FGA_MODEL_ID"));

        OpenFgaClient fgaClient = new OpenFgaClient(config);

        configureOpenTelemetry();

        // Your application logic here ...
    }

    public static void configureOpenTelemetry() {
        Resource resource =
            Resource.getDefault().toBuilder()
                .put(ServiceAttributes.SERVICE_NAME, "example-app")
                .put(ServiceAttributes.SERVICE_VERSION, "0.1.0")
                .build();

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
            .setResource(resource)
            .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setMeterProvider(sdkMeterProvider)
            .buildAndRegisterGlobal();

        otel = openTelemetry;
    }
}
```

### Metrics Configuration

The SDK includes a default configuration for metrics. You can override these defaults by providing your own `TelemetryConfiguration` instance.

```java
// Import the OpenFGA SDK
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.TelemetryConfiguration;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Counters;
import dev.openfga.sdk.telemetry.Histograms;
import dev.openfga.sdk.telemetry.Metric;

// Import the OpenTelemetry SDK
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Example {
    private OpenTelemetry otel;

    public static void main(String[] args) {
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(System.getenv("FGA_API_URL"))
                .storeId(System.getenv("FGA_STORE_ID"))
                .authorizationModelId(System.getenv("FGA_MODEL_ID"))
                .telemetryConfiguration(buildOpenTelemetryConfiguration()); // Supply your custom TelemetryConfiguration

        OpenFgaClient fgaClient = new OpenFgaClient(config);

        configureOpenTelemetry();

        // Your application logic here ...
    }

    public static TelemetryConfiguration buildOpenTelemetryConfiguration() {
        Map<Attribute, Optional<Object>> attributes = new HashMap<>();
        attributes.put(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, Optional.empty());
        attributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, Optional.empty());
        attributes.put(Attributes.FGA_CLIENT_REQUEST_MODEL_ID, Optional.empty());
        attributes.put(Attributes.FGA_CLIENT_REQUEST_STORE_ID, Optional.empty());
        attributes.put(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, Optional.empty());
        attributes.put(Attributes.HTTP_HOST, Optional.empty());
        attributes.put(Attributes.HTTP_REQUEST_RESEND_COUNT, Optional.empty());
        attributes.put(Attributes.HTTP_RESPONSE_STATUS_CODE, Optional.empty());
        attributes.put(Attributes.URL_FULL, Optional.empty());
        attributes.put(Attributes.URL_SCHEME, Optional.empty());
        attributes.put(Attributes.USER_AGENT, Optional.empty());

        Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();
        metrics.put(Counters.CREDENTIALS_REQUEST, attributes);
        metrics.put(Histograms.QUERY_DURATION, attributes);
        metrics.put(Histograms.REQUEST_DURATION, attributes);

        return new TelemetryConfiguration(metrics);
    }

    public static void configureOpenTelemetry() {
        Resource resource =
            Resource.getDefault().toBuilder()
                .put(ServiceAttributes.SERVICE_NAME, "example-app")
                .put(ServiceAttributes.SERVICE_VERSION, "0.1.0")
                .build();

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
            .setResource(resource)
            .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setMeterProvider(sdkMeterProvider)
            .buildAndRegisterGlobal();

        otel = openTelemetry;
    }
}
```
