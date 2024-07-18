package dev.openfga.sdk.telemetry;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import io.opentelemetry.api.common.AttributeKey;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a collection of attributes used for telemetry purposes.
 */
public class Attributes {
    /**
     * Attribute representing the model ID of a request.
     */
    public static final Attribute REQUEST_MODEL_ID = new Attribute("fga-client.request.model_id");

    /**
     * Attribute representing the method of a request.
     */
    public static final Attribute REQUEST_METHOD = new Attribute("fga-client.request.method");

    /**
     * Attribute representing the store ID of a request.
     */
    public static final Attribute REQUEST_STORE_ID = new Attribute("fga-client.request.store_id");

    /**
     * Attribute representing the client ID of a request.
     */
    public static final Attribute REQUEST_CLIENT_ID = new Attribute("fga-client.request.client_id");

    /**
     * Attribute representing the number of retries for a request.
     */
    public static final Attribute REQUEST_RETRIES = new Attribute("fga-client.request.retries");

    /**
     * Attribute representing the model ID of a response.
     */
    public static final Attribute RESPONSE_MODEL_ID = new Attribute("fga-client.response.model_id");

    /**
     * Attribute representing the user of a client.
     */
    public static final Attribute CLIENT_USER = new Attribute("fga-client.user");

    /**
     * Attribute representing the host of an HTTP request.
     */
    public static final Attribute HTTP_HOST = new Attribute("http.host");

    /**
     * Attribute representing the method of an HTTP request.
     */
    public static final Attribute HTTP_METHOD = new Attribute("http.method");

    /**
     * Attribute representing the status code of an HTTP response.
     */
    public static final Attribute HTTP_STATUS_CODE = new Attribute("http.status_code");

    /**
     * Prepares the attributes for OpenTelemetry publishing by converting them into the expected format.
     *
     * @param attributes the attributes to prepare
     *
     * @return the prepared attributes
     */
    public static io.opentelemetry.api.common.Attributes prepare(Map<Attribute, String> attributes) {
        io.opentelemetry.api.common.AttributesBuilder builder = io.opentelemetry.api.common.Attributes.builder();

        attributes.forEach((key, value) -> {
            builder.put(AttributeKey.stringKey(key.getName()), value);
        });

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
            attributes.put(HTTP_STATUS_CODE, String.valueOf(response.statusCode()));

            String responseModelId = response.headers()
                    .firstValue("openfga-authorization-model-id")
                    .orElse(null);

            if (responseModelId != null) {
                attributes.put(RESPONSE_MODEL_ID, responseModelId);
            }
        }

        if (credentials != null && credentials.getCredentialsMethod() == CredentialsMethod.CLIENT_CREDENTIALS) {
            attributes.put(REQUEST_CLIENT_ID, credentials.getClientCredentials().getClientId());
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
            attributes.put(HTTP_STATUS_CODE, String.valueOf(response.getStatusCode()));

            List<String> responseModelIdList =
                    response.getHeaders().getOrDefault("openfga-authorization-model-id", null);
            String responseModelId = responseModelIdList != null ? responseModelIdList.get(0) : null;

            if (responseModelId != null) {
                attributes.put(RESPONSE_MODEL_ID, responseModelId);
            }
        }

        if (credentials != null && credentials.getCredentialsMethod() == CredentialsMethod.CLIENT_CREDENTIALS) {
            attributes.put(REQUEST_CLIENT_ID, credentials.getClientCredentials().getClientId());
        }

        return attributes;
    }
}
