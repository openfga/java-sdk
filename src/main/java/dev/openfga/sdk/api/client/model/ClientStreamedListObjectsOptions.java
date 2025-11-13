package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.configuration.AdditionalHeadersSupplier;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import java.util.Map;

/**
 * Options for the streamedListObjects API call.
 *
 * <p>This class allows you to configure the streaming request with:
 * <ul>
 *   <li>Authorization model ID - Override the default model ID for this request</li>
 *   <li>Consistency preference - Specify the desired consistency level</li>
 *   <li>Additional headers - Include custom HTTP headers in the request</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * ClientStreamedListObjectsOptions options = new ClientStreamedListObjectsOptions()
 *     .authorizationModelId("custom-model-id")
 *     .consistency(ConsistencyPreference.HIGHER_CONSISTENCY);
 * </pre>
 */
public class ClientStreamedListObjectsOptions implements AdditionalHeadersSupplier {
    private String authorizationModelId;
    private ConsistencyPreference consistency;
    private Map<String, String> additionalHeaders;

    public ClientStreamedListObjectsOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientStreamedListObjectsOptions consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    public ClientStreamedListObjectsOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }
}
