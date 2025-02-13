package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import dev.openfga.sdk.api.configuration.TelemetryConfiguration;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.*;
import org.junit.jupiter.api.Test;

class AttributesTest {

    @Test
    void testPrepare() {
        // Arrange
        Map<Attribute, String> attributes = new HashMap<>();
        attributes.put(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, "client-id-value");
        Metric metric = Histograms.QUERY_DURATION;

        Map<Metric, Map<Attribute, Optional<Object>>> metricsMap = new HashMap<>();
        Map<Attribute, Optional<Object>> attributeMap = new HashMap<>();
        attributeMap.put(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, Optional.of("config-value"));
        metricsMap.put(metric, attributeMap);
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration(metricsMap);

        Configuration configuration = new Configuration();
        configuration.telemetryConfiguration(telemetryConfiguration);

        // Act
        io.opentelemetry.api.common.Attributes result = Attributes.prepare(attributes, metric, configuration);

        // Assert
        AttributesBuilder builder = io.opentelemetry.api.common.Attributes.builder();
        builder.put(AttributeKey.stringKey(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID.getName()), "client-id-value");
        io.opentelemetry.api.common.Attributes expected = builder.build();

        assertEquals(expected, result);
    }

    @Test
    void testPrepare_filtersAttributesFromDefaults() {
        // Arrange

        // sent by default
        Map<Attribute, String> defaultAttributes = new HashMap<>();
        for (Map.Entry<Attribute, Optional<Object>> entry :
                TelemetryConfiguration.defaultAttributes().entrySet()) {
            defaultAttributes.put(entry.getKey(), entry.getKey().toString() + "-value");
        }

        // not sent by default
        Map<Attribute, String> nonDefaultAttributes = new HashMap<>();
        nonDefaultAttributes.put(Attributes.FGA_CLIENT_USER, "user-value");

        Map<Attribute, String> attributes = new HashMap<>();
        attributes.putAll(defaultAttributes);
        attributes.putAll(nonDefaultAttributes);

        Metric metric = Histograms.QUERY_DURATION;

        Configuration configuration = new Configuration();
        configuration.telemetryConfiguration(new TelemetryConfiguration());

        // Act
        io.opentelemetry.api.common.Attributes result = Attributes.prepare(attributes, metric, configuration);

        // Assert
        AttributesBuilder builder = io.opentelemetry.api.common.Attributes.builder();
        for (Map.Entry<Attribute, String> entry : defaultAttributes.entrySet()) {
            builder.put(AttributeKey.stringKey(entry.getKey().getName()), entry.getValue());
        }
        io.opentelemetry.api.common.Attributes expected = builder.build();

        assertEquals(expected, result);
    }

    @Test
    void testFromHttpResponse() {
        // Arrange
        HttpResponse<?> response = mock(HttpResponse.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(response.headers()).thenReturn(headers);
        when(headers.firstValue("openfga-authorization-model-id")).thenReturn(Optional.of("model-id-value"));
        when(response.statusCode()).thenReturn(200);

        Credentials credentials = mock(Credentials.class);
        ClientCredentials clientCredentials = mock(ClientCredentials.class);
        when(credentials.getCredentialsMethod()).thenReturn(CredentialsMethod.CLIENT_CREDENTIALS);
        when(credentials.getClientCredentials()).thenReturn(clientCredentials);
        when(clientCredentials.getClientId()).thenReturn("client-id-value");

        // Act
        Map<Attribute, String> result = Attributes.fromHttpResponse(response, credentials);

        // Assert
        Map<Attribute, String> expected = new HashMap<>();
        expected.put(Attributes.HTTP_RESPONSE_STATUS_CODE, "200");
        expected.put(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, "model-id-value");
        expected.put(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, "client-id-value");

        assertEquals(expected, result);
    }

    @Test
    void testFromApiResponse() {
        // Arrange
        ApiResponse<?> response = mock(ApiResponse.class);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("openfga-authorization-model-id", Collections.singletonList("model-id-value"));
        when(response.getHeaders()).thenReturn(headers);
        when(response.getStatusCode()).thenReturn(200);

        Credentials credentials = mock(Credentials.class);
        ClientCredentials clientCredentials = mock(ClientCredentials.class);
        when(credentials.getCredentialsMethod()).thenReturn(CredentialsMethod.CLIENT_CREDENTIALS);
        when(credentials.getClientCredentials()).thenReturn(clientCredentials);
        when(clientCredentials.getClientId()).thenReturn("client-id-value");

        // Act
        Map<Attribute, String> result = Attributes.fromApiResponse(response, credentials);

        // Assert
        Map<Attribute, String> expected = new HashMap<>();
        expected.put(Attributes.HTTP_RESPONSE_STATUS_CODE, "200");
        expected.put(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, "model-id-value");
        expected.put(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, "client-id-value");

        assertEquals(expected, result);
    }
}
