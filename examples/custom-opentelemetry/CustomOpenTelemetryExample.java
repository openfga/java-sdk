package dev.openfga.sdk.example.custom.telemetry;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.TelemetryConfiguration;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.ResourceAttributes;

/**
 * Example showing how to provide a custom OpenTelemetry instance to the OpenFGA SDK.
 * This gives you complete control over telemetry configuration, exporters, and providers.
 */
public class CustomOpenTelemetryExample {

    public static void main(String[] args) throws Exception {
        // Example 1: Custom OpenTelemetry with Prometheus exporter
        customOpenTelemetryWithPrometheus();
        
        // Example 2: Multiple clients with different telemetry configs
        multipleClientsWithDifferentTelemetry();
        
        // Example 3: No-op telemetry for testing
        noOpTelemetryForTesting();
    }

    /**
     * Example 1: Create a custom OpenTelemetry instance with Prometheus exporter
     */
    private static void customOpenTelemetryWithPrometheus() throws Exception {
        System.out.println("=== Custom OpenTelemetry with Prometheus ===");
        
        // Create a custom resource
        Resource resource = Resource.getDefault().toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, "my-fga-app")
                .put(ResourceAttributes.SERVICE_VERSION, "1.0.0")
                .put(ResourceAttributes.SERVICE_INSTANCE_ID, "instance-1")
                .build();

        // Create custom meter provider with Prometheus exporter
        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(
                    PrometheusHttpServer.builder()
                        .setPort(9464) // Custom port for this app
                        .build()
                )
                .setResource(resource)
                .build();

        // Build custom OpenTelemetry instance
        OpenTelemetry customOpenTelemetry = OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .build();

        // Configure OpenFGA SDK to use custom OpenTelemetry instance
        TelemetryConfiguration telemetryConfig = new TelemetryConfiguration()
                .openTelemetry(customOpenTelemetry);

        ClientConfiguration clientConfig = new ClientConfiguration()
                .apiUrl("http://localhost:8080")
                .storeId("your-store-id")
                .telemetryConfiguration(telemetryConfig);

        OpenFgaClient client = new OpenFgaClient(clientConfig);
        
        System.out.println("✅ OpenFGA client created with custom OpenTelemetry!");
        System.out.println("📊 Prometheus metrics available at: http://localhost:9464/metrics");
        
        // Your application logic here...
        // All SDK telemetry will now use your custom OpenTelemetry instance
    }

    /**
     * Example 2: Multiple clients with different telemetry configurations
     */
    private static void multipleClientsWithDifferentTelemetry() throws Exception {
        System.out.println("\n=== Multiple Clients with Different Telemetry ===");
        
        // Client 1: Production telemetry with detailed metrics
        OpenTelemetry prodTelemetry = createProductionTelemetry();
        TelemetryConfiguration prodConfig = new TelemetryConfiguration()
                .openTelemetry(prodTelemetry);
        
        OpenFgaClient prodClient = new OpenFgaClient(new ClientConfiguration()
                .apiUrl("https://prod-api.example.com")
                .storeId("prod-store")
                .telemetryConfiguration(prodConfig));

        // Client 2: Development telemetry with console output
        OpenTelemetry devTelemetry = createDevelopmentTelemetry();
        TelemetryConfiguration devConfig = new TelemetryConfiguration()
                .openTelemetry(devTelemetry);
        
        OpenFgaClient devClient = new OpenFgaClient(new ClientConfiguration()
                .apiUrl("http://localhost:8080")
                .storeId("dev-store")
                .telemetryConfiguration(devConfig));

        // Client 3: Uses global OpenTelemetry (backward compatibility)
        OpenFgaClient defaultClient = new OpenFgaClient(new ClientConfiguration()
                .apiUrl("http://localhost:8080")
                .storeId("default-store"));
        
        System.out.println("✅ Created three clients with different telemetry setups!");
    }

    /**
     * Example 3: No-op telemetry for testing scenarios
     */
    private static void noOpTelemetryForTesting() throws Exception {
        System.out.println("\n=== No-op Telemetry for Testing ===");
        
        // Create a no-op OpenTelemetry instance for testing
        OpenTelemetry noOpTelemetry = OpenTelemetry.noop();
        
        TelemetryConfiguration testConfig = new TelemetryConfiguration()
                .openTelemetry(noOpTelemetry);

        OpenFgaClient testClient = new OpenFgaClient(new ClientConfiguration()
                .apiUrl("http://localhost:8080")
                .storeId("test-store")
                .telemetryConfiguration(testConfig));
        
        System.out.println("✅ Test client created with no-op telemetry (no metrics will be generated)");
    }

    private static OpenTelemetry createProductionTelemetry() {
        // Production telemetry setup - could include OTLP exporters, custom sampling, etc.
        Resource resource = Resource.getDefault().toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, "production-fga-service")
                .put(ResourceAttributes.DEPLOYMENT_ENVIRONMENT, "production")
                .build();

        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(
                    PrometheusHttpServer.builder()
                        .setPort(9090)
                        .build()
                )
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .build();
    }

    private static OpenTelemetry createDevelopmentTelemetry() {
        // Development telemetry setup - could include logging exporters, debug options, etc.
        Resource resource = Resource.getDefault().toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, "dev-fga-service")
                .put(ResourceAttributes.DEPLOYMENT_ENVIRONMENT, "development")
                .build();

        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(
                    PrometheusHttpServer.builder()
                        .setPort(9091)
                        .build()
                )
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .build();
    }
}
