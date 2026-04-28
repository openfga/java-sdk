package dev.openfga.sdk.api.auth;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.*;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.pgssoft.httpclient.HttpClientMock;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@WireMockTest
class OAuth2ClientTest {
    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";
    private static final String AUDIENCE = "audience";
    private static final String SCOPES = "scope1 scope2";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String ACCESS_TOKEN = "0123456789";

    private final ObjectMapper mapper = new ObjectMapper();
    private HttpClientMock mockHttpClient;

    private static Stream<Arguments> apiTokenIssuers() {
        return Stream.of(
                Arguments.of("issuer.fga.example", "https://issuer.fga.example/oauth/token"),
                Arguments.of("https://issuer.fga.example", "https://issuer.fga.example/oauth/token"),
                Arguments.of("https://issuer.fga.example/", "https://issuer.fga.example/oauth/token"),
                Arguments.of("https://issuer.fga.example:8080", "https://issuer.fga.example:8080/oauth/token"),
                Arguments.of("https://issuer.fga.example:8080/", "https://issuer.fga.example:8080/oauth/token"),
                Arguments.of("issuer.fga.example/some_endpoint", "https://issuer.fga.example/some_endpoint"),
                Arguments.of("https://issuer.fga.example/some_endpoint", "https://issuer.fga.example/some_endpoint"),
                Arguments.of(
                        "https://issuer.fga.example:8080/some_endpoint",
                        "https://issuer.fga.example:8080/some_endpoint"));
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @MethodSource("apiTokenIssuers")
    public void exchangeAuth0Token(String apiTokenIssuer, String tokenEndpointUrl) throws Exception {
        // Given
        OAuth2Client auth0 = newAuth0Client(apiTokenIssuer);

        String responseBody = String.format("{\"access_token\":\"%s\"}", ACCESS_TOKEN);
        mockHttpClient
                .onPost(tokenEndpointUrl)
                .withBody(allOf(
                        containsString(String.format("client_id=%s", CLIENT_ID)),
                        containsString(String.format("client_secret=%s", CLIENT_SECRET)),
                        containsString(String.format("audience=%s", AUDIENCE)),
                        containsString(String.format("grant_type=%s", GRANT_TYPE))))
                .doReturn(200, responseBody);

        // When
        String result = auth0.getAccessToken().get();

        // Then
        mockHttpClient
                .verify()
                .post(tokenEndpointUrl)
                .withBody(allOf(
                        containsString(String.format("client_id=%s", CLIENT_ID)),
                        containsString(String.format("client_secret=%s", CLIENT_SECRET)),
                        containsString(String.format("audience=%s", AUDIENCE)),
                        containsString(String.format("grant_type=%s", GRANT_TYPE))))
                .withHeader("Content-Type", "application/x-www-form-urlencoded")
                .called();
        assertEquals(ACCESS_TOKEN, result);
    }

    @ParameterizedTest
    @MethodSource("apiTokenIssuers")
    public void exchangeOAuth2Token(String apiTokenIssuer, String tokenEndpointUrl) throws Exception {
        // Given
        OAuth2Client auth0 = newOAuth2Client(apiTokenIssuer);

        String responseBody = String.format("{\"access_token\":\"%s\"}", ACCESS_TOKEN);
        mockHttpClient
                .onPost(tokenEndpointUrl)
                .withBody(allOf(
                        containsString(String.format("client_id=%s", CLIENT_ID)),
                        containsString(String.format("client_secret=%s", CLIENT_SECRET)),
                        containsString(String.format("scope=%s", urlEncode(SCOPES))),
                        containsString(String.format("grant_type=%s", GRANT_TYPE))))
                .doReturn(200, responseBody);

        // When
        String result = auth0.getAccessToken().get();

        // Then
        mockHttpClient
                .verify()
                .post(tokenEndpointUrl)
                .withBody(allOf(
                        containsString(String.format("client_id=%s", CLIENT_ID)),
                        containsString(String.format("client_secret=%s", CLIENT_SECRET)),
                        containsString(String.format("scope=%s", urlEncode(SCOPES))),
                        containsString(String.format("grant_type=%s", GRANT_TYPE))))
                .withHeader("Content-Type", "application/x-www-form-urlencoded")
                .called();
        assertEquals(ACCESS_TOKEN, result);
    }

    @Test
    public void exchangeOAuth2TokenWithRetriesSuccess(WireMockRuntimeInfo wm) throws Exception {
        // Return 429 initially
        stubFor(post(urlEqualTo("/oauth/token"))
                .inScenario("retries")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(jsonResponse("{\"code\":\"rate_limited\"}", 429))
                .willSetStateTo("rate limited once"));

        // Then return 500 with Retry-After header
        stubFor(post(urlEqualTo("/oauth/token"))
                .inScenario("retries")
                .whenScenarioStateIs("rate limited once")
                .willReturn(jsonResponse("{\"code\":\"rate_limited\"}", 500)
                        .withHeader(FgaConstants.RETRY_AFTER_HEADER_NAME, "1"))
                .willSetStateTo("rate limited twice"));

        // Finally return 200
        stubFor(post(urlEqualTo("/oauth/token"))
                .inScenario("retries")
                .whenScenarioStateIs("rate limited twice")
                .willReturn(ok(String.format("{\"access_token\":\"%s\"}", ACCESS_TOKEN))));

        OAuth2Client auth0 = newOAuth2Client(wm.getHttpBaseUrl(), false);

        String result = auth0.getAccessToken().get();

        assertEquals(ACCESS_TOKEN, result);
        verify(3, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void exchangeOAuth2TokenWithRetriesFailure(WireMockRuntimeInfo wm) throws Exception {
        stubFor(post(urlEqualTo("/oauth/token")).willReturn(jsonResponse("{\"code\":\"rate_limited\"}", 429)));

        OAuth2Client auth0 = newOAuth2Client(wm.getHttpBaseUrl(), false);

        var exception = assertThrows(java.util.concurrent.ExecutionException.class, () -> auth0.getAccessToken()
                .get());

        assertTrue(exception.getMessage().contains("FgaApiRateLimitExceededError"));
        assertTrue(exception.getMessage().contains("exchangeToken"));
        assertTrue(exception.getMessage().contains("HTTP 429"));
        verify(3, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void exchangeOAuth2Token_concurrentRequests_singleExchange(WireMockRuntimeInfo wm) throws Exception {
        // Stub with a delay so concurrent threads pile up before the first exchange completes.
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(ok(String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", ACCESS_TOKEN))
                        .withFixedDelay(100)));

        OAuth2Client client = newOAuth2Client(wm.getHttpBaseUrl(), false);

        int threadCount = 5;
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        List<String> tokens = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                        try {
                            startGate.await();
                            tokens.add(client.getAccessToken().get());
                        } catch (Exception e) {
                            failures.add(e);
                        } finally {
                            done.countDown();
                        }
                    })
                    .start();
        }

        startGate.countDown();
        assertTrue(done.await(3, TimeUnit.SECONDS), "threads did not complete in time");

        assertEquals(List.of(), failures, "no thread should have thrown");
        assertEquals(threadCount, tokens.size(), "all threads should have received a token");
        assertTrue(tokens.stream().allMatch(ACCESS_TOKEN::equals), "all threads should have received the same token");
        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    /**
     * After a successful exchange, subsequent calls must hit the cached snapshot and avoid the
     * network entirely. This guards the lock-free hot path in {@link OAuth2Client#getAccessToken()}.
     */
    @Test
    void exchangeOAuth2Token_cachedAcrossCalls_noSecondRequest(WireMockRuntimeInfo wm) throws Exception {
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(ok(String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", ACCESS_TOKEN))));

        OAuth2Client client = newOAuth2Client(wm.getHttpBaseUrl(), false);

        // Prime the cache.
        assertEquals(ACCESS_TOKEN, client.getAccessToken().get());

        // Many subsequent calls should all be served from the snapshot.
        for (int i = 0; i < 20; i++) {
            assertEquals(ACCESS_TOKEN, client.getAccessToken().get());
        }

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    /**
     * Deterministic regression test for the post-exchange race that the synchronized cold path
     * is meant to close.
     *
     * <p>Race being pinned: thread A reads an invalid snapshot, parks; thread B completes a full
     * exchange (writes snapshot, clears the in-flight gate); thread A then reaches the
     * acquisition gate. With the original CAS-only logic, A would have started a redundant
     * second exchange. With the synchronized re-check, A must observe the freshly-written
     * snapshot and return the cached token without contacting the IdP.
     *
     * <p>Determinism is achieved via a package-private {@code beforeAcquireHook} test seam in
     * {@link OAuth2Client}: thread A is parked at the hook between its lock-free snapshot read
     * and the synchronized gate; thread B runs end-to-end with the hook disarmed; A is then
     * released. No sleeps, no thread-scheduling assumptions.
     *
     * <p>Asserts: exactly one HTTP exchange total, both threads receive the same token.
     */
    @Test
    void exchangeOAuth2Token_postCompletionRace_noSecondExchange(WireMockRuntimeInfo wm) throws Exception {
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(ok(String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", ACCESS_TOKEN))));

        OAuth2Client client = newOAuth2Client(wm.getHttpBaseUrl(), false);

        CountDownLatch threadAParked = new CountDownLatch(1);
        CountDownLatch releaseThreadA = new CountDownLatch(1);

        // Arm the hook: the next caller (thread A) will park here right between its lock-free
        // snapshot check and entering the synchronized gate.
        client.beforeAcquireHook = () -> {
            threadAParked.countDown();
            try {
                if (!releaseThreadA.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("thread A was never released");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        // Thread A: enters, sees invalid snapshot, parks at the hook.
        AtomicReference<String> tokenA = new AtomicReference<>();
        AtomicReference<Throwable> failureA = new AtomicReference<>();
        Thread a = new Thread(() -> {
            try {
                tokenA.set(client.getAccessToken().get(5, TimeUnit.SECONDS));
            } catch (Throwable t) {
                failureA.set(t);
            }
        }, "race-thread-A");
        a.start();

        assertTrue(threadAParked.await(2, TimeUnit.SECONDS), "thread A never reached the hook");

        // Disarm the hook so thread B (this thread) is *not* trapped, then perform a full
        // exchange end-to-end. After this returns: snapshot is valid, inFlight is null.
        client.beforeAcquireHook = OAuth2Client.NO_OP_HOOK;
        String tokenB = client.getAccessToken().get(5, TimeUnit.SECONDS);
        assertEquals(ACCESS_TOKEN, tokenB);
        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));

        // Now release A. It will enter acquireToken() in *exactly* the post-completion state
        // (snapshot valid, inFlight null) that the original CAS-only code mishandled.
        releaseThreadA.countDown();
        a.join(5_000);
        assertFalse(a.isAlive(), "thread A did not finish");
        assertNull(failureA.get(), "thread A threw");
        assertEquals(ACCESS_TOKEN, tokenA.get());

        // The decisive assertion: A must NOT have triggered a second exchange.
        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void apiTokenIssuer_invalidScheme() {
        // When
        var exception =
                assertThrows(FgaInvalidParameterException.class, () -> newAuth0Client("ftp://issuer.fga.example"));

        // Then
        assertEquals("Required parameter scheme was invalid when calling apiTokenIssuer.", exception.getMessage());
    }

    private static Stream<Arguments> invalidApiTokenIssuers() {
        return Stream.of(
                Arguments.of("://issuer.fga.example"),
                Arguments.of("http://issuer.fga.example#bad#fragment"),
                Arguments.of("http://issuer.fga.example/space in path"),
                Arguments.of("http://"));
    }

    @ParameterizedTest
    @MethodSource("invalidApiTokenIssuers")
    public void apiTokenIssuers_invalidURI(String invalidApiTokenIssuer) {
        // When
        var exception = assertThrows(FgaInvalidParameterException.class, () -> newAuth0Client(invalidApiTokenIssuer));

        // Then
        assertEquals(
                "Required parameter apiTokenIssuer was invalid when calling ClientCredentials.",
                exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    private OAuth2Client newAuth0Client(String apiTokenIssuer) throws FgaInvalidParameterException {
        return newClientCredentialsClient(
                apiTokenIssuer,
                new Credentials(new ClientCredentials()
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .apiAudience(AUDIENCE)
                        .apiTokenIssuer(apiTokenIssuer)),
                true);
    }

    private OAuth2Client newOAuth2Client(String apiTokenIssuer) throws FgaInvalidParameterException {
        return newClientCredentialsClient(
                apiTokenIssuer,
                new Credentials(new ClientCredentials()
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .scopes(SCOPES)
                        .apiTokenIssuer(apiTokenIssuer)),
                true);
    }

    private OAuth2Client newOAuth2Client(String apiTokenIssuer, Boolean useMockHttpClient)
            throws FgaInvalidParameterException {
        return newClientCredentialsClient(
                apiTokenIssuer,
                new Credentials(new ClientCredentials()
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .scopes(SCOPES)
                        .apiTokenIssuer(apiTokenIssuer)),
                useMockHttpClient);
    }

    private OAuth2Client newClientCredentialsClient(
            String apiTokenIssuer, Credentials credentials, Boolean useMockHttpClient)
            throws FgaInvalidParameterException {
        System.setProperty("HttpRequestAttempt.debug-logging", "enable");

        var configuration = new Configuration()
                .apiUrl("")
                .credentials(credentials)
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(10));

        // If requested, enable the HttpClientMock and set that as the HttpClient to use in ApiClient
        ApiClient apiClient;
        if (useMockHttpClient) {
            mockHttpClient = new HttpClientMock();
            mockHttpClient.debugOn();

            apiClient = mock(ApiClient.class);
            when(apiClient.getHttpClient()).thenReturn(mockHttpClient);
            when(apiClient.getObjectMapper()).thenReturn(mapper);
        } else {
            apiClient = new ApiClient();
        }

        return new OAuth2Client(configuration, apiClient);
    }
}
