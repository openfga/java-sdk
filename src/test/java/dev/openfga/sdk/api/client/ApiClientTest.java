package dev.openfga.sdk.api.client;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pgssoft.httpclient.HttpClientMock;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.ApiException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

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

    @Nested
    class ApplyAuthHeader {

        @Test
        void none_skipsHeader() throws Exception {
            Configuration configuration =
                    new Configuration().apiUrl(FgaConstants.TEST_API_URL).credentials(new Credentials());

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));

            new ApiClient().applyAuthHeader(requestBuilder, configuration);

            assertFalse(
                    requestBuilder.build().headers().firstValue("Authorization").isPresent());
        }

        @Test
        void nullCredentials_skipsHeader() throws Exception {
            Configuration configuration = Mockito.mock(Configuration.class);
            Mockito.when(configuration.getCredentials()).thenReturn(null);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));

            new ApiClient().applyAuthHeader(requestBuilder, configuration);

            assertFalse(
                    requestBuilder.build().headers().firstValue("Authorization").isPresent());
        }

        @Test
        void nullMethod_skipsHeader() throws Exception {
            Credentials credentials = new Credentials();
            credentials.setCredentialsMethod(null);
            Configuration configuration =
                    new Configuration().apiUrl(FgaConstants.TEST_API_URL).credentials(credentials);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));

            new ApiClient().applyAuthHeader(requestBuilder, configuration);

            assertFalse(
                    requestBuilder.build().headers().firstValue("Authorization").isPresent());
        }

        @Test
        void apiToken_setsAuthHeader() throws Exception {
            String token = "static-api-token";
            Configuration configuration = new Configuration()
                    .apiUrl(FgaConstants.TEST_API_URL)
                    .credentials(new Credentials(new ApiToken(token)));

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));

            new ApiClient().applyAuthHeader(requestBuilder, configuration);

            assertEquals(
                    "Bearer " + token,
                    requestBuilder.build().headers().firstValue("Authorization").orElseThrow());
        }

        /*
         * Regression test for #330: applying auth a second time must replace, not append,
         * the Authorization header so retried requests don't ship with duplicates.
         */
        @Test
        void apiToken_replaceExistingAuthHeader() throws Exception {
            String firstToken = "first-token";
            String secondToken = "second-token";
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));

            ApiClient apiClient = new ApiClient();
            apiClient.applyAuthHeader(
                    requestBuilder,
                    new Configuration()
                            .apiUrl(FgaConstants.TEST_API_URL)
                            .credentials(new Credentials(new ApiToken(firstToken))));

            apiClient.applyAuthHeader(
                    requestBuilder,
                    new Configuration()
                            .apiUrl(FgaConstants.TEST_API_URL)
                            .credentials(new Credentials(new ApiToken(secondToken))));

            List<String> authHeaders = requestBuilder.build().headers().allValues("Authorization");
            assertEquals(1, authHeaders.size());
            assertEquals("Bearer " + secondToken, authHeaders.get(0));
        }

        @Test
        void clientCredentials_failureAsApiException() {
            HttpClientMock mockHttpClient = new HttpClientMock();
            mockHttpClient
                    .onPost(String.format("%s/oauth/token", FgaConstants.TEST_ISSUER_URL))
                    .doReturnStatus(401);

            HttpClient.Builder mockBuilder = mockHttpClientBuilder(mockHttpClient);
            ApiClient apiClient = new ApiClient(mockBuilder);

            Configuration configuration = new Configuration()
                    .apiUrl(FgaConstants.TEST_API_URL)
                    .maxRetries(0)
                    .credentials(new Credentials(new ClientCredentials()
                            .clientId("cid")
                            .clientSecret("secret")
                            .apiAudience("aud")
                            .apiTokenIssuer(FgaConstants.TEST_ISSUER_URL)));

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));

            assertThrows(ApiException.class, () -> apiClient.applyAuthHeader(requestBuilder, configuration));
            assertFalse(
                    requestBuilder.build().headers().firstValue("Authorization").isPresent());
        }

        @Test
        void clientCredentials_setsAuthHeader() throws Exception {
            String clientId = "some-client-id";
            String clientSecret = "some-client-secret";
            String apiAudience = "some-audience";
            String exchangedToken = "exchanged-access-token";

            HttpClientMock mockHttpClient = new HttpClientMock();
            mockHttpClient
                    .onPost(String.format("%s/oauth/token", FgaConstants.TEST_ISSUER_URL))
                    .withBody(allOf(
                            containsString("client_id=" + clientId),
                            containsString("client_secret=" + clientSecret),
                            containsString("audience=" + apiAudience),
                            containsString("grant_type=client_credentials")))
                    .doReturn(200, String.format("{\"access_token\":\"%s\",\"expires_in\":3600}", exchangedToken));

            HttpClient.Builder mockBuilder = mockHttpClientBuilder(mockHttpClient);
            ApiClient apiClient = new ApiClient(mockBuilder);

            Configuration configuration = new Configuration()
                    .apiUrl(FgaConstants.TEST_API_URL)
                    .credentials(new Credentials(new ClientCredentials()
                            .clientId(clientId)
                            .clientSecret(clientSecret)
                            .apiAudience(apiAudience)
                            .apiTokenIssuer(FgaConstants.TEST_ISSUER_URL)));

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));
            apiClient.applyAuthHeader(requestBuilder, configuration);

            assertEquals(
                    "Bearer " + exchangedToken,
                    requestBuilder.build().headers().firstValue("Authorization").orElseThrow());

            // A second call should reuse the cached token and not hit the issuer again.
            HttpRequest.Builder secondBuilder = HttpRequest.newBuilder().uri(URI.create(FgaConstants.TEST_API_URL));
            apiClient.applyAuthHeader(secondBuilder, configuration);
            assertEquals(
                    "Bearer " + exchangedToken,
                    secondBuilder.build().headers().firstValue("Authorization").orElseThrow());

            mockHttpClient
                    .verify()
                    .post(String.format("%s/oauth/token", FgaConstants.TEST_ISSUER_URL))
                    .called(1);
        }
    }

    private static HttpClient.Builder mockHttpClientBuilder(HttpClient client) {
        HttpClient.Builder builder = Mockito.mock(HttpClient.Builder.class);
        Mockito.when(builder.build()).thenReturn(client);
        Mockito.when(builder.executor(ArgumentMatchers.any())).thenReturn(builder);
        return builder;
    }
}
