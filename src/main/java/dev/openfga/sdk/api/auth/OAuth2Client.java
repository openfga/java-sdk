/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 1.x
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package dev.openfga.sdk.api.auth;

import dev.openfga.sdk.api.client.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Telemetry;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OAuth2Client {
    private static final String DEFAULT_API_TOKEN_ISSUER_PATH = "/oauth/token";

    private final ApiClient apiClient;
    private final AccessToken token = new AccessToken();
    private final CredentialsFlowRequest authRequest;
    private final Configuration config;
    private final Telemetry telemetry;

    /**
     * Initializes a new instance of the {@link OAuth2Client} class
     *
     * @param configuration Configuration, including credentials, that can be used to retrieve an access tokens
     */
    public OAuth2Client(Configuration configuration, ApiClient apiClient) throws FgaInvalidParameterException {
        var clientCredentials = configuration.getCredentials().getClientCredentials();

        this.apiClient = apiClient;
        this.authRequest =
                new CredentialsFlowRequest(clientCredentials.getClientId(), clientCredentials.getClientSecret());
        this.authRequest.setAudience(clientCredentials.getApiAudience());
        this.authRequest.setScope(clientCredentials.getScopes());
        this.config = new Configuration()
                .apiUrl(buildApiTokenIssuer(clientCredentials.getApiTokenIssuer()))
                .connectTimeout(configuration.getConnectTimeout())
                .maxRetries(configuration.getMaxRetries())
                .minimumRetryDelay(configuration.getMinimumRetryDelay())
                .telemetryConfiguration(configuration.getTelemetryConfiguration());
        this.telemetry = new Telemetry(this.config);
    }

    /**
     * Gets an access token, handling exchange when necessary. The access token is naively cached in memory until it
     * expires.
     *
     * @return An access token in a {@link CompletableFuture}
     */
    public CompletableFuture<String> getAccessToken() throws FgaInvalidParameterException, ApiException {
        if (!token.isValid()) {
            return exchangeToken().thenCompose(response -> {
                token.setToken(response.getAccessToken());
                token.setExpiresAt(Instant.now().plusSeconds(response.getExpiresInSeconds()));

                Map<Attribute, String> attributesMap = new HashMap<>();

                telemetry.metrics().credentialsRequest(1L, attributesMap);

                return CompletableFuture.completedFuture(token.getToken());
            });
        }

        return CompletableFuture.completedFuture(token.getToken());
    }

    /**
     * Exchange a client id and client secret for an access token.
     * @return The credentials flow response
     */
    private CompletableFuture<CredentialsFlowResponse> exchangeToken()
            throws ApiException, FgaInvalidParameterException {

        HttpRequest.Builder requestBuilder =
                ApiClient.formRequestBuilder("POST", "", this.authRequest.buildFormRequestBody(), config);
        HttpRequest request = requestBuilder.build();

        return new HttpRequestAttempt<>(request, "exchangeToken", CredentialsFlowResponse.class, apiClient, config)
                .attemptHttpRequest()
                .thenApply(ApiResponse::getData);
    }

    private static String buildApiTokenIssuer(String issuer) throws FgaInvalidParameterException {
        URI uri;
        try {
            uri = URI.create(issuer);
        } catch (IllegalArgumentException cause) {
            throw new FgaInvalidParameterException("apiTokenIssuer", "ClientCredentials", cause);
        }

        var scheme = uri.getScheme();
        if (scheme == null) {
            uri = URI.create("https://" + issuer);
        } else if (!"https".equals(scheme) && !"http".equals(scheme)) {
            throw new FgaInvalidParameterException("scheme", "apiTokenIssuer");
        }

        if (uri.getPath().isEmpty() || uri.getPath().equals("/")) {
            uri = URI.create(uri.getScheme() + "://" + uri.getAuthority() + DEFAULT_API_TOKEN_ISSUER_PATH);
        }

        return uri.toString();
    }
}
