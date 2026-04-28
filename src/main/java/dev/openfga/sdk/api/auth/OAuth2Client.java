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
     * Test-only seam invoked on the cold path after the lock-free snapshot check fails and
     * before entering the synchronized acquisition gate. Defaults to a no-op. Used by tests to
     * deterministically interleave threads around the post-exchange race window. Not part of the
     * public API.
     */
    static final Runnable NO_OP_HOOK = () -> {};

    volatile Runnable beforeAcquireHook = NO_OP_HOOK;

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
     * <p>The hot path (valid cached token) is lock-free. The cold path serializes the
     * "is snapshot valid? / is there an in-flight exchange? / publish my promise" decision
     * under a monitor so that:
     * <ul>
     *   <li>at most one exchange is started per expiry,</li>
     *   <li>joiners always observe the same in-flight promise that the owner will complete.</li>
     * </ul>
     * The exchange itself (the HTTP round-trip) runs asynchronously outside the monitor.
     *
     * @return An access token in a {@link CompletableFuture}
     */
    public CompletableFuture<String> getAccessToken() throws FgaInvalidParameterException, ApiException {
        // Lock-free hot path: a valid cached token short-circuits everything.
        AccessToken current = snapshot.get();
        if (current.isValid()) {
            return CompletableFuture.completedFuture(current.token());
        }
        // Cold-path test seam (no-op in production).
        beforeAcquireHook.run();
        return acquireToken();
    }

    /**
     * Cold path: snapshot is missing or expired. Serialized to guarantee a single in-flight
     * exchange and to avoid the join-vs-clear race that an atomic-CAS-only approach is prone to.
     */
    private synchronized CompletableFuture<String> acquireToken()
            throws FgaInvalidParameterException, ApiException {
        // Re-check under the monitor: another thread may have just refreshed the snapshot.
        AccessToken current = snapshot.get();
        if (current.isValid()) {
            return CompletableFuture.completedFuture(current.token());
        }

        // Join an existing exchange if one is already in flight.
        CompletableFuture<String> existing = inFlight.get();
        if (existing != null) {
            return existing;
        }

        // This thread owns the exchange. Publish the promise so concurrent callers can join.
        CompletableFuture<String> promise = new CompletableFuture<>();
        inFlight.set(promise);

        try {
            exchangeToken().whenComplete((response, ex) -> {
                // Completion runs asynchronously, outside the monitor. That's fine: state
                // transitions here (snapshot, inFlight, promise) are each individually
                // thread-safe, and the monitor only guards the decision to *start* an exchange.
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
            // Synchronous failure to even dispatch the request: clear the gate and propagate.
            inFlight.set(null);
            promise.completeExceptionally(e);
            throw e;
        }

        return promise;
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
