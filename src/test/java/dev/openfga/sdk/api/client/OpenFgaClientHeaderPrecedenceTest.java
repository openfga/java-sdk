package dev.openfga.sdk.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientStreamedListObjectsOptions;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientListStoresOptions;
import dev.openfga.sdk.api.configuration.Credentials;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Headers sent on the wire when the default headers, the per-request additional headers and the credentials
 * name the same header. Uses a real ApiClient, so request interceptors take part.
 */
@WireMockTest
class OpenFgaClientHeaderPrecedenceTest {
    private static final String STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";
    private static final UrlPathPattern STORES = urlPathEqualTo("/stores");
    private static final UrlPathPattern STREAMED_LIST_OBJECTS =
            urlPathEqualTo("/stores/" + STORE_ID + "/streamed-list-objects");

    private String apiUrl;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        apiUrl = wm.getHttpBaseUrl();
        stubFor(get(STORES)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"stores\":[],\"continuation_token\":\"\"}")));
        stubFor(post(STREAMED_LIST_OBJECTS)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/x-ndjson")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n")));
    }

    private static List<String> sent(UrlPathPattern path, String header) {
        List<LoggedRequest> requests = findAll(anyRequestedFor(path));
        assertEquals(1, requests.size());
        return requests.get(0).getHeaders().getHeader(header).values();
    }

    @Test
    void defaultHeaderIsSent() throws Exception {
        var config = new ClientConfiguration().apiUrl(apiUrl).defaultHeaders(Map.of("X-Custom-Header", "default"));

        new OpenFgaClient(config).listStores().get();

        assertEquals(List.of("default"), sent(STORES, "X-Custom-Header"));
    }

    @Test
    void perRequestHeaderOverridesDefaultHeader() throws Exception {
        var config = new ClientConfiguration().apiUrl(apiUrl).defaultHeaders(Map.of("X-Custom-Header", "default"));

        new OpenFgaClient(config)
                .listStores(new ClientListStoresOptions().additionalHeaders(Map.of("X-Custom-Header", "per-request")))
                .get();

        assertEquals(List.of("per-request"), sent(STORES, "X-Custom-Header"));
    }

    @Test
    void perRequestAuthorizationReplacesCredentialsHeader() throws Exception {
        var config = new ClientConfiguration()
                .apiUrl(apiUrl)
                .credentials(new Credentials(new ApiToken("configured-token")));

        new OpenFgaClient(config)
                .listStores(new ClientListStoresOptions()
                        .additionalHeaders(Map.of("Authorization", "Bearer per-request-token")))
                .get();

        assertEquals(List.of("Bearer per-request-token"), sent(STORES, "Authorization"));
    }

    @Test
    void streamedListObjectsSendsAdditionalHeaders() throws Exception {
        var config = new ClientConfiguration()
                .apiUrl(apiUrl)
                .storeId(STORE_ID)
                .defaultHeaders(Map.of("X-Custom-Header", "default"));

        new OpenFgaClient(config)
                .streamedListObjects(
                        new ClientListObjectsRequest()
                                .user("user:anne")
                                .relation("viewer")
                                .type("document"),
                        new ClientStreamedListObjectsOptions()
                                .additionalHeaders(Map.of("X-Custom-Header", "per-request", "X-Extra", "extra")),
                        response -> {})
                .get();

        assertEquals(List.of("per-request"), sent(STREAMED_LIST_OBJECTS, "X-Custom-Header"));
        assertEquals(List.of("extra"), sent(STREAMED_LIST_OBJECTS, "X-Extra"));
    }

    @Test
    void creatingClientsDoesNotRepeatDefaultHeaders() throws Exception {
        var config = new ClientConfiguration().apiUrl(apiUrl).defaultHeaders(Map.of("X-Custom-Header", "default"));
        var apiClient = new ApiClient();

        new OpenFgaClient(config, apiClient);
        new OpenFgaClient(config, apiClient).listStores().get();

        assertEquals(List.of("default"), sent(STORES, "X-Custom-Header"));
        assertNull(apiClient.getRequestInterceptor());
    }
}
