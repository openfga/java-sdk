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
