package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.openfga.sdk.api.auth.OAuth2Client;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.ApiException;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;
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
    public void getAccessToken_withNone_returnsNull() throws Exception {
        ApiClient apiClient = new ApiClient();
        Configuration config = new Configuration().apiUrl("https://test.example");
        // Default Credentials() has method NONE
        assertNull(apiClient.getAccessToken(config));
    }

    @Test
    public void getAccessToken_withApiToken_returnsToken() throws Exception {
        ApiClient apiClient = new ApiClient();
        Configuration config = new Configuration()
                .apiUrl("https://test.example")
                .credentials(new Credentials(new ApiToken("my-static-token")));
        assertEquals("my-static-token", apiClient.getAccessToken(config));
    }

    @Test
    public void getAccessToken_withClientCredentials_returnsTokenFromOAuth2Client() throws Exception {
        ApiClient apiClient = new ApiClient();
        OAuth2Client mockOAuth2 = mock(OAuth2Client.class);
        when(mockOAuth2.getAccessToken()).thenReturn(CompletableFuture.completedFuture("oauth2-token-abc"));
        apiClient.setOAuth2Client(mockOAuth2);

        ClientCredentials clientCreds = new ClientCredentials()
                .clientId("id")
                .clientSecret("secret")
                .apiTokenIssuer("issuer.example")
                .apiAudience("audience");
        Configuration config =
                new Configuration().apiUrl("https://test.example").credentials(new Credentials(clientCreds));

        assertEquals("oauth2-token-abc", apiClient.getAccessToken(config));
        verify(mockOAuth2, times(1)).getAccessToken();
    }

    @Test
    public void getAccessToken_withClientCredentials_noOAuth2Client_throwsIllegalState() {
        ApiClient apiClient = new ApiClient();
        // No setOAuth2Client called

        ClientCredentials clientCreds = new ClientCredentials()
                .clientId("id")
                .clientSecret("secret")
                .apiTokenIssuer("issuer.example")
                .apiAudience("audience");
        Configuration config =
                new Configuration().apiUrl("https://test.example").credentials(new Credentials(clientCreds));

        assertThrows(IllegalStateException.class, () -> apiClient.getAccessToken(config));
    }

    @Test
    public void getAccessToken_withClientCredentials_oAuth2Fails_throwsApiException() throws Exception {
        ApiClient apiClient = new ApiClient();
        OAuth2Client mockOAuth2 = mock(OAuth2Client.class);
        when(mockOAuth2.getAccessToken())
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("token exchange failed")));
        apiClient.setOAuth2Client(mockOAuth2);

        ClientCredentials clientCreds = new ClientCredentials()
                .clientId("id")
                .clientSecret("secret")
                .apiTokenIssuer("issuer.example")
                .apiAudience("audience");
        Configuration config =
                new Configuration().apiUrl("https://test.example").credentials(new Credentials(clientCreds));

        assertThrows(ApiException.class, () -> apiClient.getAccessToken(config));
    }
}
