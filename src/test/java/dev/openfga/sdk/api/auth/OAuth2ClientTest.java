package dev.openfga.sdk.api.auth;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgssoft.httpclient.HttpClientMock;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @ParameterizedTest
    @MethodSource("apiTokenIssuers")
    public void exchangeAuth0Token(String apiTokenIssuer, String tokenEndpointUrl) throws Exception {
        // Given
        OAuth2Client auth0 = newAuth0Client(apiTokenIssuer);
        String expectedPostBody = String.format(
                "{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"audience\":\"%s\",\"grant_type\":\"%s\"}",
                CLIENT_ID, CLIENT_SECRET, AUDIENCE, GRANT_TYPE);
        String responseBody = String.format("{\"access_token\":\"%s\"}", ACCESS_TOKEN);
        mockHttpClient.onPost(tokenEndpointUrl).withBody(is(expectedPostBody)).doReturn(200, responseBody);

        // When
        String result = auth0.getAccessToken().get();

        // Then
        mockHttpClient
                .verify()
                .post(tokenEndpointUrl)
                .withBody(is(expectedPostBody))
                .called();
        assertEquals(ACCESS_TOKEN, result);
    }

    @ParameterizedTest
    @MethodSource("apiTokenIssuers")
    public void exchangeOAuth2Token(String apiTokenIssuer, String tokenEndpointUrl) throws Exception {
        // Given
        OAuth2Client oAuth2 = newOAuth2Client(apiTokenIssuer);
        String expectedPostBody = String.format(
                "{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"scope\":\"%s\",\"grant_type\":\"%s\"}",
                CLIENT_ID, CLIENT_SECRET, SCOPES, GRANT_TYPE);
        String responseBody = String.format("{\"access_token\":\"%s\"}", ACCESS_TOKEN);
        mockHttpClient.onPost(tokenEndpointUrl).withBody(is(expectedPostBody)).doReturn(200, responseBody);

        // When
        String result = oAuth2.getAccessToken().get();

        // Then
        mockHttpClient
                .verify()
                .post(tokenEndpointUrl)
                .withBody(is(expectedPostBody))
                .called();
        assertEquals(ACCESS_TOKEN, result);
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
                        .apiTokenIssuer(apiTokenIssuer)));
    }

    private OAuth2Client newOAuth2Client(String apiTokenIssuer) throws FgaInvalidParameterException {
        return newClientCredentialsClient(
                apiTokenIssuer,
                new Credentials(new ClientCredentials()
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .scopes(SCOPES)
                        .apiTokenIssuer(apiTokenIssuer)));
    }

    private OAuth2Client newClientCredentialsClient(String apiTokenIssuer, Credentials credentials)
            throws FgaInvalidParameterException {
        System.setProperty("HttpRequestAttempt.debug-logging", "enable");

        mockHttpClient = new HttpClientMock();
        mockHttpClient.debugOn();

        var configuration = new Configuration().apiUrl("").credentials(credentials);

        var apiClient = mock(ApiClient.class);
        when(apiClient.getHttpClient()).thenReturn(mockHttpClient);
        when(apiClient.getObjectMapper()).thenReturn(mapper);

        return new OAuth2Client(configuration, apiClient);
    }
}
