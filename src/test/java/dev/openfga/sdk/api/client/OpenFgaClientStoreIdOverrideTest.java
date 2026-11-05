package dev.openfga.sdk.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.openfga.sdk.api.client.model.ClientBatchCheckItem;
import dev.openfga.sdk.api.client.model.ClientBatchCheckRequest;
import dev.openfga.sdk.api.client.model.ClientCheckRequest;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientListRelationsRequest;
import dev.openfga.sdk.api.client.model.ClientReadRequest;
import dev.openfga.sdk.api.client.model.ClientStreamedListObjectsOptions;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientBatchCheckOptions;
import dev.openfga.sdk.api.configuration.ClientCheckOptions;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientListObjectsOptions;
import dev.openfga.sdk.api.configuration.ClientListRelationsOptions;
import dev.openfga.sdk.api.configuration.ClientReadOptions;
import dev.openfga.sdk.api.configuration.ClientWriteOptions;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Requests are sent to the store set in the per-request options, and to the configured store otherwise.
 */
@WireMockTest
class OpenFgaClientStoreIdOverrideTest {
    private static final String CONFIGURED_STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";
    private static final String OVERRIDE_STORE_ID = "01HVMMBCMGZNT3SED4Z17ECXCA";

    private String apiUrl;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        apiUrl = wm.getHttpBaseUrl();
        stubFor(post(urlPathMatching("/stores/[^/]+/check"))
                .willReturn(okJson("{\"allowed\":true,\"resolution\":\"\"}")));
        stubFor(post(urlPathMatching("/stores/[^/]+/batch-check")).willReturn(okJson("{\"result\":{}}")));
        stubFor(post(urlPathMatching("/stores/[^/]+/write")).willReturn(okJson("{}")));
        stubFor(post(urlPathMatching("/stores/[^/]+/read"))
                .willReturn(okJson("{\"tuples\":[],\"continuation_token\":\"\"}")));
        stubFor(post(urlPathMatching("/stores/[^/]+/list-objects")).willReturn(okJson("{\"objects\":[]}")));
        stubFor(post(urlPathMatching("/stores/[^/]+/streamed-list-objects"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/x-ndjson")
                        .withBody("{\"result\":{\"object\":\"document:1\"}}\n")));
    }

    private OpenFgaClient client(String storeId) throws FgaInvalidParameterException {
        return new OpenFgaClient(new ClientConfiguration().apiUrl(apiUrl).storeId(storeId));
    }

    private static ClientCheckRequest checkRequest(String relation) {
        return new ClientCheckRequest().user("user:anne").relation(relation)._object("document:1");
    }

    @Test
    void usesTheConfiguredStoreWithoutAnOverride() throws Exception {
        client(CONFIGURED_STORE_ID).check(checkRequest("viewer")).get();

        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + CONFIGURED_STORE_ID + "/check")));
    }

    @Test
    void checkUsesTheStoreFromTheOptions() throws Exception {
        client(CONFIGURED_STORE_ID)
                .check(checkRequest("viewer"), new ClientCheckOptions().storeId(OVERRIDE_STORE_ID))
                .get();

        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/check")));
        verify(0, postRequestedFor(urlPathEqualTo("/stores/" + CONFIGURED_STORE_ID + "/check")));
    }

    @Test
    void theOverrideWorksWithoutAConfiguredStore() throws Exception {
        client(null)
                .check(checkRequest("viewer"), new ClientCheckOptions().storeId(OVERRIDE_STORE_ID))
                .get();

        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/check")));
    }

    @Test
    void failsWithoutAConfiguredStoreOrAnOverride() throws Exception {
        assertThrows(
                FgaInvalidParameterException.class, () -> client(null).check(checkRequest("viewer"), null));
    }

    @Test
    void writeReadAndListObjectsUseTheStoreFromTheOptions() throws Exception {
        var fga = client(CONFIGURED_STORE_ID);
        var tuple = new ClientTupleKey().user("user:anne").relation("viewer")._object("document:1");

        fga.write(new ClientWriteRequest().writes(List.of(tuple)), new ClientWriteOptions().storeId(OVERRIDE_STORE_ID))
                .get();
        fga.read(new ClientReadRequest(), new ClientReadOptions().storeId(OVERRIDE_STORE_ID))
                .get();
        fga.listObjects(
                        new ClientListObjectsRequest()
                                .user("user:anne")
                                .relation("viewer")
                                .type("document"),
                        new ClientListObjectsOptions().storeId(OVERRIDE_STORE_ID))
                .get();

        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/write")));
        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/read")));
        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/list-objects")));
    }

    @Test
    void listRelationsPassesTheStoreToItsChecks() throws Exception {
        client(CONFIGURED_STORE_ID)
                .listRelations(
                        new ClientListRelationsRequest()
                                .user("user:anne")
                                .relations(List.of("viewer", "editor"))
                                ._object("document:1"),
                        new ClientListRelationsOptions().storeId(OVERRIDE_STORE_ID))
                .get();

        verify(2, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/check")));
        verify(0, postRequestedFor(urlPathEqualTo("/stores/" + CONFIGURED_STORE_ID + "/check")));
    }

    @Test
    void batchCheckUsesTheStoreFromTheOptions() throws Exception {
        client(CONFIGURED_STORE_ID)
                .batchCheck(
                        new ClientBatchCheckRequest()
                                .checks(List.of(new ClientBatchCheckItem()
                                        .user("user:anne")
                                        .relation("viewer")
                                        ._object("document:1")
                                        .correlationId("1"))),
                        new ClientBatchCheckOptions().storeId(OVERRIDE_STORE_ID))
                .get();

        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/batch-check")));
    }

    @Test
    void streamedListObjectsUsesTheStoreFromTheOptions() throws Exception {
        client(CONFIGURED_STORE_ID)
                .streamedListObjects(
                        new ClientListObjectsRequest()
                                .user("user:anne")
                                .relation("viewer")
                                .type("document"),
                        new ClientStreamedListObjectsOptions().storeId(OVERRIDE_STORE_ID),
                        response -> {})
                .get();

        verify(1, postRequestedFor(urlPathEqualTo("/stores/" + OVERRIDE_STORE_ID + "/streamed-list-objects")));
    }
}
