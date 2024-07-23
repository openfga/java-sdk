package dev.openfga.sdk.telemetry;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import io.opentelemetry.api.common.AttributeKey;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class represents a collection of attributes used for telemetry purposes.
 */
public class Attributes {

    /**
     * The client ID used in the request, if applicable.
     */
    public static final Attribute FGA_CLIENT_REQUEST_CLIENT_ID = new Attribute("fga-client.request.client_id");

    /**
     * The FGA method/action of the request.
     */
    public static final Attribute FGA_CLIENT_REQUEST_METHOD = new Attribute("fga-client.request.method");

    /**
     * The authorization model ID used in the request, if applicable.
     */
    public static final Attribute FGA_CLIENT_REQUEST_MODEL_ID = new Attribute("fga-client.request.model_id");

    /**
     * The store ID used in the request, if applicable.
     */
    public static final Attribute FGA_CLIENT_REQUEST_STORE_ID = new Attribute("fga-client.request.store_id");

    /**
     * The authorization model ID used by the server when evaluating the request, if applicable.
     */
    public static final Attribute FGA_CLIENT_RESPONSE_MODEL_ID = new Attribute("fga-client.response.model_id");

    /**
     * The user associated with the action of the request, if applicable.
     */
    public static final Attribute FGA_CLIENT_USER = new Attribute("fga-client.user");

    /**
     * The HTTP host used in the request.
     */
    public static final Attribute HTTP_HOST = new Attribute("http.host");

    /**
     * The HTTP method used in the request.
     */
    public static final Attribute HTTP_REQUEST_METHOD = new Attribute("http.request.method");

    /**
     * The number of times the request was retried.
     */
    public static final Attribute HTTP_REQUEST_RESEND_COUNT = new Attribute("http.request.resend_count");

    /**
     * The HTTP status code returned by the server for the request.
     */
    public static final Attribute HTTP_RESPONSE_STATUS_CODE = new Attribute("http.response.status_code");

    /**
     * The scheme used in the request.
     */
    public static final Attribute URL_SCHEME = new Attribute("url.scheme");

    /**
     * The complete URL used in the request.
     */
    public static final Attribute URL_FULL = new Attribute("url.full");

    /**
     * The user agent used in the request.
     */
    public static final Attribute USER_AGENT = new Attribute("user_agent.original");

    /**
     * Prepares the attributes for OpenTelemetry publishing by converting them into the expected format.
     *
     * @param attributes the attributes to prepare
     *
     * @return the prepared attributes
     */
    public static io.opentelemetry.api.common.Attributes prepare(
            Map<Attribute, String> attributes, Metric metric, Configuration configuration) {
        if (attributes == null
                || attributes.isEmpty()
                || configuration == null
                || configuration.getTelemetryConfiguration() == null
                || configuration.getTelemetryConfiguration().metrics() == null
                || configuration.getTelemetryConfiguration().metrics().isEmpty()
                || !configuration.getTelemetryConfiguration().metrics().containsKey(metric)
                || configuration
                        .getTelemetryConfiguration()
                        .metrics()
                        .get(metric)
                        .isEmpty()) {
            return io.opentelemetry.api.common.Attributes.empty();
        }

        Map<Attribute, Optional<Object>> configAllowedAttributes =
                configuration.getTelemetryConfiguration().metrics().get(metric);

        io.opentelemetry.api.common.AttributesBuilder builder = io.opentelemetry.api.common.Attributes.builder();

        for (Map.Entry<Attribute, Optional<Object>> configAllowedAttr : configAllowedAttributes.entrySet()) {
            Attribute attr = configAllowedAttr.getKey();

            if (!attributes.containsKey(attr)) {
                continue;
            }

            String attrVal = attributes.getOrDefault(attr, "");

            if (!isNullOrWhitespace(attrVal)) {
                builder.put(AttributeKey.stringKey(attr.getName()), attrVal);
            }
        }

        return builder.build();
    }

    /**
     * Converts an HTTP response and credentials into a map of attributes for telemetry purposes.
     *
     * @param response    the HTTP response
     * @param credentials the credentials
     *
     * @return the map of formatted attributes
     */
    public static Map<Attribute, String> fromHttpResponse(HttpResponse<?> response, Credentials credentials) {
        Map<Attribute, String> attributes = new HashMap<>();

        if (response != null) {
            attributes.put(HTTP_RESPONSE_STATUS_CODE, String.valueOf(response.statusCode()));

            String responseModelId = response.headers()
                    .firstValue("openfga-authorization-model-id")
                    .orElse(null);

            if (!isNullOrWhitespace(responseModelId)) {
                attributes.put(FGA_CLIENT_RESPONSE_MODEL_ID, responseModelId);
            }
        }

        if (credentials != null && credentials.getCredentialsMethod() == CredentialsMethod.CLIENT_CREDENTIALS) {
            if (!isNullOrWhitespace(credentials.getClientCredentials().getClientId())) {
                attributes.put(
                        FGA_CLIENT_REQUEST_CLIENT_ID,
                        credentials.getClientCredentials().getClientId());
            }
        }

        return attributes;
    }

    /**
     * Converts an API response and credentials into a map of attributes for telemetry purposes.
     *
     * @param response    the API response
     * @param credentials the credentials
     *
     * @return the map of formatted attributes
     */
    public static Map<Attribute, String> fromApiResponse(ApiResponse<?> response, Credentials credentials) {
        Map<Attribute, String> attributes = new HashMap<>();

        if (response != null) {
            attributes.put(HTTP_RESPONSE_STATUS_CODE, String.valueOf(response.getStatusCode()));

            List<String> responseModelIdList =
                    response.getHeaders().getOrDefault("openfga-authorization-model-id", null);
            String responseModelId = responseModelIdList != null ? responseModelIdList.get(0) : null;

            if (!isNullOrWhitespace(responseModelId)) {
                attributes.put(FGA_CLIENT_RESPONSE_MODEL_ID, responseModelId);
            }
        }

        if (credentials != null && credentials.getCredentialsMethod() == CredentialsMethod.CLIENT_CREDENTIALS) {
            if (!isNullOrWhitespace(credentials.getClientCredentials().getClientId())) {
                attributes.put(
                        FGA_CLIENT_REQUEST_CLIENT_ID,
                        credentials.getClientCredentials().getClientId());
            }
        }

        return attributes;
    }
}
