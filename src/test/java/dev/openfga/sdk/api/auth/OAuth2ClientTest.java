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
     * Regression: after a successful exchange, a second wave of concurrent callers must see the
     * cached snapshot and NOT trigger a redundant exchange.
     * This covers a race where thread A reads an invalid snapshot, thread B completes the exchange,
     * and thread A then enters the slow path synchronized block - but still returns the cached
     * token without issuing another request.
     */
    @Test
    void getAccessToken_cachedTokenHit(WireMockRuntimeInfo wm) throws Exception {
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(ok(String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", ACCESS_TOKEN))
                        .withFixedDelay(100)));

        OAuth2Client client = newOAuth2Client(wm.getHttpBaseUrl(), false);

        // Wave 1 — triggers the only exchange.
        int wave1Count = 3;
        CountDownLatch wave1Start = new CountDownLatch(1);
        CountDownLatch wave1Done = new CountDownLatch(wave1Count);
        List<String> wave1Tokens = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < wave1Count; i++) {
            new Thread(() -> {
                        try {
                            wave1Start.await();
                            wave1Tokens.add(client.getAccessToken().get());
                        } catch (Exception e) {
                            failures.add(e);
                        } finally {
                            wave1Done.countDown();
                        }
                    })
                    .start();
        }
        wave1Start.countDown();
        assertTrue(wave1Done.await(2, TimeUnit.SECONDS), "wave 1 did not complete in time");
        assertTrue(failures.isEmpty(), "wave 1 should not have failures");

        // Wave 2 — arrives after the exchange has completed; must use the cached token.
        int wave2Count = 5;
        CountDownLatch wave2Start = new CountDownLatch(1);
        CountDownLatch wave2Done = new CountDownLatch(wave2Count);
        List<String> wave2Tokens = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < wave2Count; i++) {
            new Thread(() -> {
                        try {
                            wave2Start.await();
                            wave2Tokens.add(client.getAccessToken().get());
                        } catch (Exception e) {
                            failures.add(e);
                        } finally {
                            wave2Done.countDown();
                        }
                    })
                    .start();
        }
        wave2Start.countDown();
        assertTrue(wave2Done.await(2, TimeUnit.SECONDS), "wave 2 did not complete in time");

        assertEquals(List.of(), failures, "no thread should have thrown");
        assertEquals(wave1Count, wave1Tokens.size());
        assertEquals(wave2Count, wave2Tokens.size());
        assertTrue(wave1Tokens.stream().allMatch(ACCESS_TOKEN::equals));
        assertTrue(wave2Tokens.stream().allMatch(ACCESS_TOKEN::equals));

        // Only one exchange ever happened across both waves.
        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    /**
     * Regression: a late wave of callers that arrives while the exchange is still in-flight
     * must join the existing future rather than starting a second exchange.
     */
    @Test
    void getAccessToken_joinInFlightExchange(WireMockRuntimeInfo wm) throws Exception {
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(ok(String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", ACCESS_TOKEN))
                        .withFixedDelay(300)));

        OAuth2Client client = newOAuth2Client(wm.getHttpBaseUrl(), false);

        List<String> allTokens = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());

        // Wave 1 — triggers the exchange (300 ms delay).
        int wave1Count = 2;
        CountDownLatch wave1Start = new CountDownLatch(1);
        CountDownLatch allDone = new CountDownLatch(wave1Count + 3);

        for (int i = 0; i < wave1Count; i++) {
            new Thread(() -> {
                        try {
                            wave1Start.await();
                            allTokens.add(client.getAccessToken().get());
                        } catch (Exception e) {
                            failures.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    })
                    .start();
        }
        wave1Start.countDown();

        // Wave 2 — arrives 50 ms later while exchange is still in-flight.
        Thread.sleep(50);
        int wave2Count = 3;
        for (int i = 0; i < wave2Count; i++) {
            new Thread(() -> {
                        try {
                            allTokens.add(client.getAccessToken().get());
                        } catch (Exception e) {
                            failures.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    })
                    .start();
        }

        assertTrue(allDone.await(5, TimeUnit.SECONDS), "threads did not complete in time");
        assertEquals(List.of(), failures, "no thread should have thrown");
        assertEquals(wave1Count + wave2Count, allTokens.size());
        assertTrue(allTokens.stream().allMatch(ACCESS_TOKEN::equals));
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
