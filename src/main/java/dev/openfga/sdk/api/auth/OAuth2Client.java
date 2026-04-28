package dev.openfga.sdk.api.auth;

import dev.openfga.sdk.api.client.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.telemetry.Telemetry;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class OAuth2Client {
    private static final String DEFAULT_API_TOKEN_ISSUER_PATH = "/oauth/token";

    private final ApiClient apiClient;
    private final AtomicReference<AccessToken> snapshot = new AtomicReference<>(AccessToken.EMPTY);
    private final AtomicReference<CompletableFuture<String>> inFlight = new AtomicReference<>();
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
     * Gets an access token, handling exchange when necessary. The token is cached as an immutable
     * snapshot until it expires. Concurrent calls are deduplicated: only one exchange is in flight
     * at a time; other callers join the same future rather than issuing redundant requests.
     *
     * @return An access token in a {@link CompletableFuture}
     */
    public CompletableFuture<String> getAccessToken() throws FgaInvalidParameterException, ApiException {
        // Fast path (lock-free): return cached token if still valid.
        AccessToken current = snapshot.get();
        if (current.isValid()) {
            return CompletableFuture.completedFuture(current.token());
        }

        // Slow path: decide under the lock who starts the exchange.
        synchronized (this) {
            // Double-check: another thread may have refreshed while we waited.
            AccessToken rechecked = snapshot.get();
            if (rechecked.isValid()) {
                return CompletableFuture.completedFuture(rechecked.token());
            }

            // Join an existing in-flight exchange.
            CompletableFuture<String> existing = inFlight.get();
            if (existing != null) {
                return existing;
            }

            // Start a new exchange and publish the future so other callers join it.
            CompletableFuture<String> promise = new CompletableFuture<>();
            inFlight.set(promise);

            try {
                exchangeToken().whenComplete((response, ex) -> {
                    if (ex != null) {
                        inFlight.set(null);
                        promise.completeExceptionally(ex);
                    } else {
                        String token = response.getAccessToken();
                        // Write snapshot before clearing the gate so any new caller that arrives
                        // after inFlight becomes null immediately sees a valid token.
                        snapshot.set(new AccessToken(token, Instant.now().plusSeconds(response.getExpiresInSeconds())));
                        inFlight.set(null);
                        promise.complete(token);
                        telemetry.metrics().credentialsRequest(1L, new HashMap<>());
                    }
                });
            } catch (Exception e) {
                inFlight.set(null);
                promise.completeExceptionally(e);
                throw e;
            }

            return promise;
        }
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

        return new HttpRequestAttempt<>(
                        request, "exchangeToken", CredentialsFlowResponse.class, apiClient, config, telemetry)
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
