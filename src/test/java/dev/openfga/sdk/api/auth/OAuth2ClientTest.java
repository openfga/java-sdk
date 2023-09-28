package dev.openfga.sdk.api.auth;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgssoft.httpclient.HttpClientMock;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OAuth2ClientTest {
    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";
    private static final String AUDIENCE = "audience";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String API_TOKEN_ISSUER = "test.fga.dev";
    private static final String POST_URL = "https://" + API_TOKEN_ISSUER + "/oauth/token";
    private static final String ACCESS_TOKEN = "0123456789";

    private final ObjectMapper mapper = new ObjectMapper();
    private HttpClientMock mockHttpClient;

    private OAuth2Client oAuth2;

    @BeforeEach
    public void setup() throws FgaInvalidParameterException {
        mockHttpClient = new HttpClientMock();

        var credentials = new Credentials(new ClientCredentials()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .apiAudience(AUDIENCE)
                .apiTokenIssuer(API_TOKEN_ISSUER));

        var configuration = new Configuration().apiUrl("").credentials(credentials);

        oAuth2 = new OAuth2Client(configuration, mockHttpClient, mapper);
    }

    @Test
    public void exchangeToken() throws Exception {
        // Given
        String expectedPostBody = String.format(
                "{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"audience\":\"%s\",\"grant_type\":\"%s\"}",
                CLIENT_ID, CLIENT_SECRET, AUDIENCE, GRANT_TYPE);
        String responseBody = String.format("{\"access_token\":\"%s\"}", ACCESS_TOKEN);
        mockHttpClient.onPost(POST_URL).withBody(is(expectedPostBody)).doReturn(200, responseBody);

        // When
        String result = oAuth2.getAccessToken().get();

        // Then
        mockHttpClient.verify().post(POST_URL).withBody(is(expectedPostBody)).called();
        assertEquals(ACCESS_TOKEN, result);
    }
}
