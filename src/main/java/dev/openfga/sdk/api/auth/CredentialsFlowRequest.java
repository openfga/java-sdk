package dev.openfga.sdk.api.auth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A credentials flow request. It contains a Client ID and Secret that can be exchanged for an access token.
 * <p>
 * {@see "https://auth0.com/docs/get-started/authentication-and-authorization-flow/client-credentials-flow"}
 */
class CredentialsFlowRequest {
    public static final String CLIENT_ID_PARAM_NAME = "client_id";
    public static final String CLIENT_SECRET_PARAM_NAME = "client_secret";
    public static final String AUDIENCE_PARAM_NAME = "audience";
    public static final String SCOPE_PARAM_NAME = "scope";
    public static final String GRANT_TYPE_PARAM_NAME = "grant_type";

    private final Map<String, String> parameters = new HashMap<>();

    public CredentialsFlowRequest(String clientId, String clientSecret) {
        this.parameters.put(CLIENT_ID_PARAM_NAME, clientId);
        this.parameters.put(CLIENT_SECRET_PARAM_NAME, clientSecret);
        this.parameters.put(GRANT_TYPE_PARAM_NAME, "client_credentials");
    }

    public void setScope(String scope) {
        this.parameters.put(SCOPE_PARAM_NAME, scope);
    }

    public void setAudience(String audience) {
        this.parameters.put(AUDIENCE_PARAM_NAME, audience);
    }

    public String buildFormRequestBody() {
        return parameters.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
