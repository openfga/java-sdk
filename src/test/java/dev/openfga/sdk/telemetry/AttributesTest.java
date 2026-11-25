package dev.openfga.sdk.telemetry;

import static io.opentelemetry.api.common.AttributeKey.longKey;
import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import dev.openfga.sdk.api.configuration.TelemetryConfiguration;
import io.opentelemetry.api.common.AttributesBuilder;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.*;
import org.junit.jupiter.api.Test;

class AttributesTest {
    @Test
    void shouldPrepareAttributes() {
        // given
        Map<Attribute, String> attributes = Map.of(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, "client-id-value");
        Metric metric = Histograms.QUERY_DURATION;

        Map<Attribute, Optional<Object>> attributeMap =
                Map.of(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, Optional.of("config-value"));
        Map<Metric, Map<Attribute, Optional<Object>>> metricsMap = Map.of(metric, attributeMap);

        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration(metricsMap);
        Configuration configuration = new Configuration().telemetryConfiguration(telemetryConfiguration);

        // when
        io.opentelemetry.api.common.Attributes result = Attributes.prepare(attributes, metric, configuration);

        // then
        io.opentelemetry.api.common.Attributes expected = io.opentelemetry.api.common.Attributes.builder()
                .put(stringKey(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID.getName()), "client-id-value")
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldPrepareFiltersAttributesFromDefaults() {
        // given

        // sent by default
        Map<Attribute, String> defaultAttributes = new HashMap<>();
        for (Map.Entry<Attribute, Optional<Object>> entry :
                TelemetryConfiguration.defaultAttributes().entrySet()) {
            defaultAttributes.put(entry.getKey(), entry.getKey().toString() + "-value");
        }
        defaultAttributes.put(Attributes.HTTP_REQUEST_RESEND_COUNT, "2");
        defaultAttributes.put(Attributes.HTTP_RESPONSE_STATUS_CODE, "200");

        // not sent by default
        Map<Attribute, String> nonDefaultAttributes = Map.of(Attributes.FGA_CLIENT_USER, "user-value");

        Map<Attribute, String> attributes = new HashMap<>();
        attributes.putAll(defaultAttributes);
        attributes.putAll(nonDefaultAttributes);

        Metric metric = Histograms.QUERY_DURATION;

        Configuration configuration = new Configuration();
        configuration.telemetryConfiguration(new TelemetryConfiguration());

        // when
        io.opentelemetry.api.common.Attributes result = Attributes.prepare(attributes, metric, configuration);

        // then
        AttributesBuilder builder = io.opentelemetry.api.common.Attributes.builder();
        for (Map.Entry<Attribute, String> entry : defaultAttributes.entrySet()) {
            if (entry.getKey() == Attributes.HTTP_REQUEST_RESEND_COUNT
                    || entry.getKey() == Attributes.HTTP_RESPONSE_STATUS_CODE) {
                builder.put(longKey(entry.getKey().getName()), Long.parseLong(entry.getValue()));
            } else {
                builder.put(stringKey(entry.getKey().getName()), entry.getValue());
            }
        }
        io.opentelemetry.api.common.Attributes expected = builder.build();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldPrepareNumericAttributesAsLongs() {
        // given
        Map<Attribute, String> attributes = Map.of(
                Attributes.FGA_CLIENT_REQUEST_BATCH_CHECK_SIZE, "10",
                Attributes.HTTP_REQUEST_RESEND_COUNT, "2",
                Attributes.HTTP_RESPONSE_STATUS_CODE, "404",
                Attributes.HTTP_REQUEST_METHOD, "POST");
        Metric metric = Histograms.REQUEST_DURATION;

        Map<Attribute, Optional<Object>> attributeMap = Map.of(
                Attributes.FGA_CLIENT_REQUEST_BATCH_CHECK_SIZE, Optional.empty(),
                Attributes.HTTP_REQUEST_RESEND_COUNT, Optional.empty(),
                Attributes.HTTP_RESPONSE_STATUS_CODE, Optional.empty(),
                Attributes.HTTP_REQUEST_METHOD, Optional.empty());
        Configuration configuration =
                new Configuration().telemetryConfiguration(new TelemetryConfiguration(Map.of(metric, attributeMap)));

        // when
        io.opentelemetry.api.common.Attributes result = Attributes.prepare(attributes, metric, configuration);

        // then
        io.opentelemetry.api.common.Attributes expected = io.opentelemetry.api.common.Attributes.builder()
                .put(longKey(Attributes.FGA_CLIENT_REQUEST_BATCH_CHECK_SIZE.getName()), 10L)
                .put(longKey(Attributes.HTTP_REQUEST_RESEND_COUNT.getName()), 2L)
                .put(longKey(Attributes.HTTP_RESPONSE_STATUS_CODE.getName()), 404L)
                .put(stringKey(Attributes.HTTP_REQUEST_METHOD.getName()), "POST")
                .build();
        assertThat(result).isEqualTo(expected);
        assertThat(result.get(stringKey(Attributes.HTTP_RESPONSE_STATUS_CODE.getName())))
                .isNull();
    }

    @Test
    void shouldPrepareSkipsNumericAttributesThatAreNotNumbers() {
        // given
        Map<Attribute, String> attributes = new HashMap<>();
        attributes.put(Attributes.HTTP_RESPONSE_STATUS_CODE, "not-a-number");
        attributes.put(Attributes.HTTP_REQUEST_RESEND_COUNT, "");
        attributes.put(Attributes.FGA_CLIENT_REQUEST_BATCH_CHECK_SIZE, null);
        attributes.put(Attributes.HTTP_REQUEST_METHOD, "POST");
        Metric metric = Histograms.REQUEST_DURATION;

        Map<Attribute, Optional<Object>> attributeMap = Map.of(
                Attributes.FGA_CLIENT_REQUEST_BATCH_CHECK_SIZE, Optional.empty(),
                Attributes.HTTP_REQUEST_RESEND_COUNT, Optional.empty(),
                Attributes.HTTP_RESPONSE_STATUS_CODE, Optional.empty(),
                Attributes.HTTP_REQUEST_METHOD, Optional.empty());
        Configuration configuration =
                new Configuration().telemetryConfiguration(new TelemetryConfiguration(Map.of(metric, attributeMap)));

        // when
        io.opentelemetry.api.common.Attributes result = Attributes.prepare(attributes, metric, configuration);

        // then
        io.opentelemetry.api.common.Attributes expected = io.opentelemetry.api.common.Attributes.builder()
                .put(stringKey(Attributes.HTTP_REQUEST_METHOD.getName()), "POST")
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldConvertAttributesFromHttpResponse() {
        // given
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

        // when
        Map<Attribute, String> result = Attributes.fromHttpResponse(response, credentials);

        // then
        assertThat(result)
                .containsEntry(Attributes.HTTP_RESPONSE_STATUS_CODE, "200")
                .containsEntry(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, "model-id-value")
                .containsEntry(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, "client-id-value");
    }

    @Test
    void shouldConvertAttributesFromApiResponse() {
        // given
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

        // when
        Map<Attribute, String> result = Attributes.fromApiResponse(response, credentials);

        // then
        assertThat(result)
                .containsEntry(Attributes.HTTP_RESPONSE_STATUS_CODE, "200")
                .containsEntry(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, "model-id-value")
                .containsEntry(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, "client-id-value");
    }
}
