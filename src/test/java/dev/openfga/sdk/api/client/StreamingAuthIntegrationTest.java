package dev.openfga.sdk.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for streaming auth — regression suite for openfga/java-sdk#330.
 *
 * <p>Uses WireMock as both the FGA API server and the OAuth2 token issuer so the full
 * HTTP round-trip is exercised with real sockets: client credentials exchange → token
 * caching → streaming request with Authorization header.</p>
 */
@WireMockTest
class StreamingAuthIntegrationTest {

    private static final String STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";

    // -----------------------------------------------------------------------
    // API_TOKEN credential method
    // -----------------------------------------------------------------------

    @Test
    void streamedListObjects_withApiToken_sendsAuthHeader(WireMockRuntimeInfo wm) throws Exception {
        String apiToken = "my-static-api-token";

        // Stub the streamed-list-objects endpoint — only matches if the Authorization header is present.
        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + apiToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n"
                                + "{\"result\":{\"object\":\"document:2\"}}\n")));

        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(wm.getHttpBaseUrl())
                .storeId(STORE_ID)
                .credentials(new Credentials(new ApiToken(apiToken)));

        try (var ignored = new AutoCloseableClient(config)) {
            OpenFgaClient client = ignored.client;
            List<StreamedListObjectsResponse> results = new ArrayList<>();
            client.streamedListObjects(
                            new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"),
                            results::add)
                    .get();

            assertEquals(2, results.size());
            assertEquals("document:1", results.get(0).getObject());
            assertEquals("document:2", results.get(1).getObject());
        }

        verify(postRequestedFor(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + apiToken)));
    }

    // -----------------------------------------------------------------------
    // CLIENT_CREDENTIALS credential method
    // -----------------------------------------------------------------------

    @Test
    void streamedListObjects_withClientCredentials_exchangesTokenAndSendsAuthHeader(WireMockRuntimeInfo wm)
            throws Exception {
        String exchangedToken = "exchanged-access-token-abc";

        // 1. Stub the OAuth2 token endpoint.
        stubFor(post(urlEqualTo("/oauth/token"))
                .withRequestBody(containing("grant_type=client_credentials"))
                .withRequestBody(containing("client_id=test-client-id"))
                .withRequestBody(containing("client_secret=test-client-secret"))
                .withRequestBody(containing("audience=test-audience"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.format(
                                "{\"access_token\":\"%s\",\"expires_in\":3600}", exchangedToken))));

        // 2. Stub the streaming endpoint — only matches with the exchanged bearer token.
        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + exchangedToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n")));

        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(wm.getHttpBaseUrl())
                .storeId(STORE_ID)
                .credentials(new Credentials(new ClientCredentials()
                        .clientId("test-client-id")
                        .clientSecret("test-client-secret")
                        .apiAudience("test-audience")
                        .apiTokenIssuer(wm.getHttpBaseUrl())));

        try (var ignored = new AutoCloseableClient(config)) {
            OpenFgaClient client = ignored.client;
            List<StreamedListObjectsResponse> results = new ArrayList<>();
            client.streamedListObjects(
                            new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"),
                            results::add)
                    .get();

            assertEquals(1, results.size());
            assertEquals("document:1", results.get(0).getObject());
        }

        // Token exchange happened exactly once.
        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        // Streaming request carried the bearer token.
        verify(postRequestedFor(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + exchangedToken)));
    }

    // -----------------------------------------------------------------------
    // CLIENT_CREDENTIALS: token is reused across streaming + non-streaming
    // -----------------------------------------------------------------------

    @Test
    void clientCredentials_tokenReusedAcrossStreamingAndNonStreamingCalls(WireMockRuntimeInfo wm) throws Exception {
        String exchangedToken = "shared-token-xyz";

        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.format(
                                "{\"access_token\":\"%s\",\"expires_in\":3600}", exchangedToken))));

        // Non-streaming listObjects endpoint
        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + exchangedToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"objects\":[\"document:1\"]}")));

        // Streaming endpoint
        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + exchangedToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n")));

        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(wm.getHttpBaseUrl())
                .storeId(STORE_ID)
                .credentials(new Credentials(new ClientCredentials()
                        .clientId("test-client-id")
                        .clientSecret("test-client-secret")
                        .apiAudience("test-audience")
                        .apiTokenIssuer(wm.getHttpBaseUrl())));

        try (var ignored = new AutoCloseableClient(config)) {
            OpenFgaClient client = ignored.client;

            // 1. Non-streaming call — triggers token exchange
            var listResult = client.listObjects(
                            new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"))
                    .get();
            assertEquals(1, listResult.getObjects().size());

            // 2. Streaming call — should reuse the cached token (no second exchange)
            List<StreamedListObjectsResponse> streamResults = new ArrayList<>();
            client.streamedListObjects(
                            new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"),
                            streamResults::add)
                    .get();
            assertEquals(1, streamResults.size());
        }

        // Only ONE token exchange should have happened for both calls.
        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    // -----------------------------------------------------------------------
    // No credentials — no Authorization header
    // -----------------------------------------------------------------------

    @Test
    void streamedListObjects_withoutCredentials_sendsNoAuthHeader(WireMockRuntimeInfo wm) throws Exception {
        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n")));

        ClientConfiguration config =
                new ClientConfiguration().apiUrl(wm.getHttpBaseUrl()).storeId(STORE_ID);

        try (var ignored = new AutoCloseableClient(config)) {
            OpenFgaClient client = ignored.client;
            List<StreamedListObjectsResponse> results = new ArrayList<>();
            client.streamedListObjects(
                            new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"),
                            results::add)
                    .get();
            assertEquals(1, results.size());
        }

        verify(postRequestedFor(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withoutHeader("Authorization"));
    }

    // -----------------------------------------------------------------------
    // Missing auth → 401 propagation
    // -----------------------------------------------------------------------

    @Test
    void streamedListObjects_serverReturns401_propagatesApiException(WireMockRuntimeInfo wm) {
        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .willReturn(aResponse().withStatus(401).withBody("Unauthorized")));

        ClientConfiguration config =
                new ClientConfiguration().apiUrl(wm.getHttpBaseUrl()).storeId(STORE_ID).maxRetries(0);

        assertThrows(ExecutionException.class, () -> {
            try (var ignored = new AutoCloseableClient(config)) {
                OpenFgaClient client = ignored.client;
                client.streamedListObjects(
                                new ClientListObjectsRequest()
                                        .user("user:anne")
                                        .relation("reader")
                                        .type("document"),
                                obj -> {})
                        .get();
            }
        });
    }

    // -----------------------------------------------------------------------
    // StreamingApiExecutor path — same auth fix
    // -----------------------------------------------------------------------

    @Test
    void streamingApiExecutor_withApiToken_sendsAuthHeader(WireMockRuntimeInfo wm) throws Exception {
        String apiToken = "executor-api-token";

        stubFor(post(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + apiToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n")));

        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(wm.getHttpBaseUrl())
                .storeId(STORE_ID)
                .credentials(new Credentials(new ApiToken(apiToken)));

        try (var ignored = new AutoCloseableClient(config)) {
            OpenFgaClient client = ignored.client;
            List<StreamedListObjectsResponse> results = new ArrayList<>();

            client.streamingApiExecutor(StreamedListObjectsResponse.class)
                    .stream(
                            ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                                    .body(new ClientListObjectsRequest()
                                            .user("user:anne")
                                            .relation("reader")
                                            .type("document"))
                                    .build(),
                            results::add)
                    .get();

            assertEquals(1, results.size());
        }

        verify(postRequestedFor(urlEqualTo("/stores/" + STORE_ID + "/streamed-list-objects"))
                .withHeader("Authorization", equalTo("Bearer " + apiToken)));
    }

    // -----------------------------------------------------------------------
    // ApiExecutor (non-streaming) path — same auth fix
    // -----------------------------------------------------------------------

    @Test
    void apiExecutor_withClientCredentials_sendsAuthHeader(WireMockRuntimeInfo wm) throws Exception {
        String exchangedToken = "executor-cc-token";

        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.format(
                                "{\"access_token\":\"%s\",\"expires_in\":3600}", exchangedToken))));

        stubFor(get(urlEqualTo("/stores/" + STORE_ID + "/custom-endpoint"))
                .withHeader("Authorization", equalTo("Bearer " + exchangedToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));

        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(wm.getHttpBaseUrl())
                .storeId(STORE_ID)
                .credentials(new Credentials(new ClientCredentials()
                        .clientId("test-client-id")
                        .clientSecret("test-client-secret")
                        .apiAudience("test-audience")
                        .apiTokenIssuer(wm.getHttpBaseUrl())));

        try (var ignored = new AutoCloseableClient(config)) {
            OpenFgaClient client = ignored.client;

            var response = client.apiExecutor()
                    .send(
                            ApiExecutorRequestBuilder.builder(HttpMethod.GET, "/stores/{store_id}/custom-endpoint")
                                    .build(),
                            Object.class)
                    .get();

            assertEquals(200, response.getStatusCode());
        }

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        verify(getRequestedFor(urlEqualTo("/stores/" + STORE_ID + "/custom-endpoint"))
                .withHeader("Authorization", equalTo("Bearer " + exchangedToken)));
    }

    /**
     * Thin wrapper so we can use try-with-resources to ensure cleanup,
     * though OpenFgaClient doesn't hold external resources.
     */
    private static class AutoCloseableClient implements AutoCloseable {
        final OpenFgaClient client;

        AutoCloseableClient(ClientConfiguration config) throws Exception {
            this.client = new OpenFgaClient(config);
        }

        @Override
        public void close() {
            // OpenFgaClient has no close method; this just scopes the variable.
        }
    }
}

