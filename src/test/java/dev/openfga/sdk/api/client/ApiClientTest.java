package dev.openfga.sdk.api.client;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.pgssoft.httpclient.HttpClientMock;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.constants.FgaConstants;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

class ApiClientTest {

    @Test
    public void returnSameHttpClient() {
        ApiClient apiClient = new ApiClient();
        assertEquals(apiClient.getHttpClient(), apiClient.getHttpClient());
    }

    @Test
    public void newHttpClientWhenBuilderModified() {
        ApiClient apiClient = new ApiClient();

        HttpClient client1 = apiClient.getHttpClient();
        apiClient.setHttpClientBuilder(HttpClient.newBuilder());

        assertNotEquals(client1, apiClient.getHttpClient());
    }

    @Test
    public void httpClientShouldUseHttp1ByDefault() {
        ApiClient apiClient = new ApiClient();
        assertEquals(apiClient.getHttpClient().version(), HttpClient.Version.HTTP_1_1);
    }

    @Test
    public void customHttpClientWithHttp2() {
        HttpClient.Builder builder = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2);
        ApiClient apiClient = new ApiClient(builder);
        ;
        assertEquals(apiClient.getHttpClient().version(), HttpClient.Version.HTTP_2);
    }

    @Test
    public void applyAuthHeader_none_skipsHeader() throws Exception {
        Configuration configuration =
                new Configuration().apiUrl(FgaConstants.TEST_API_URL).credentials(new Credentials());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(java.net.URI.create("http://example"));

        new ApiClient().applyAuthHeader(requestBuilder, configuration);

        assertFalse(requestBuilder.build().headers().firstValue("Authorization").isPresent());
    }

    @Test
    public void applyAuthHeader_apiToken_setsBearerHeader() throws Exception {
        String token = "static-api-token";
        Configuration configuration =
                new Configuration().apiUrl(FgaConstants.TEST_API_URL).credentials(new Credentials(new ApiToken(token)));
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(java.net.URI.create("http://example"));

        new ApiClient().applyAuthHeader(requestBuilder, configuration);

        assertEquals(
                "Bearer " + token,
                requestBuilder.build().headers().firstValue("Authorization").orElseThrow());
    }

    @Test
    public void applyAuthHeader_clientCredentials_exchangesAndSetsBearerHeader() throws Exception {
        String clientId = "some-client-id";
        String clientSecret = "some-client-secret";
        String apiAudience = "some-audience";
        String apiTokenIssuer = "oauth2.server";
        String exchangedToken = "exchanged-access-token";

        HttpClientMock mockHttpClient = new HttpClientMock();
        mockHttpClient
                .onPost(String.format("https://%s/oauth/token", apiTokenIssuer))
                .withBody(allOf(
                        containsString("client_id=" + clientId),
                        containsString("client_secret=" + clientSecret),
                        containsString("audience=" + apiAudience),
                        containsString("grant_type=client_credentials")))
                .doReturn(200, String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", exchangedToken));

        HttpClient.Builder mockBuilder = mockBuilderReturning(mockHttpClient);
        ApiClient apiClient = new ApiClient(mockBuilder);

        Configuration configuration = new Configuration()
                .apiUrl(FgaConstants.TEST_API_URL)
                .credentials(new Credentials(new ClientCredentials()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .apiAudience(apiAudience)
                        .apiTokenIssuer(apiTokenIssuer)));

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(java.net.URI.create("http://example"));
        apiClient.applyAuthHeader(requestBuilder, configuration);

        assertEquals(
                "Bearer " + exchangedToken,
                requestBuilder.build().headers().firstValue("Authorization").orElseThrow());

        // A second call should reuse the cached token and not hit the issuer again.
        HttpRequest.Builder secondBuilder = HttpRequest.newBuilder().uri(java.net.URI.create("http://example"));
        apiClient.applyAuthHeader(secondBuilder, configuration);
        assertEquals(
                "Bearer " + exchangedToken,
                secondBuilder.build().headers().firstValue("Authorization").orElseThrow());
        mockHttpClient
                .verify()
                .post(String.format("https://%s/oauth/token", apiTokenIssuer))
                .called(1);
    }

    private static HttpClient.Builder mockBuilderReturning(HttpClient client) {
        HttpClient.Builder builder = org.mockito.Mockito.mock(HttpClient.Builder.class);
        org.mockito.Mockito.when(builder.build()).thenReturn(client);
        org.mockito.Mockito.when(builder.executor(org.mockito.ArgumentMatchers.any()))
                .thenReturn(builder);
        return builder;
    }
}
