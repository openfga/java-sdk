package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.openfga.OpenFGAContainer;

/**
 * Integration tests verifying that streaming and executor code paths attach
 * the Authorization header when credentials are configured.
 *
 * Uses an OpenFGA container with pre-shared key auth enabled — any request
 * missing a valid Authorization header gets a 401.
 */
@TestInstance(Lifecycle.PER_CLASS)
@Testcontainers
public class StreamedListObjectsAuthIntegrationTest {

    private static final String PRESHARED_KEY = "integration-test-secret";

    @Container
    private static final OpenFGAContainer openfga = new OpenFGAContainer("openfga/openfga:v1.10.2")
            .withCommand("run", "--authn-method=preshared", "--authn-preshared-keys=" + PRESHARED_KEY);

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private OpenFgaClient fga;

    @BeforeAll
    public void setup() throws Exception {
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(openfga.getHttpEndpoint())
                .credentials(new Credentials(new ApiToken(PRESHARED_KEY)));

        fga = new OpenFgaClient(config);

        // Create store
        var storeResponse =
                fga.createStore(new CreateStoreRequest().name("auth-integration-test")).get();
        fga.setStoreId(storeResponse.getId());

        // Write authorization model
        String authModelJson =
                Files.readString(Paths.get("src", "test-integration", "resources", "auth-model.json"));
        var authModelRequest = mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class);
        var modelResponse = fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(modelResponse.getAuthorizationModelId());

        // Write tuples
        var tuples = List.of(
                new ClientTupleKey().user("user:alice").relation("reader")._object("document:doc1"),
                new ClientTupleKey().user("user:alice").relation("reader")._object("document:doc2"),
                new ClientTupleKey().user("user:alice").relation("reader")._object("document:doc3"));
        fga.write(new ClientWriteRequest().writes(tuples)).get();
    }

    @Test
    public void listObjects_succeeds_withAuth() throws Exception {
        // Baseline — non-streaming always worked
        var response = fga.listObjects(new ClientListObjectsRequest()
                        .user("user:alice")
                        .relation("reader")
                        .type("document"))
                .get();

        assertEquals(3, response.getObjects().size());
    }

    @Test
    public void streamedListObjects_succeeds_withAuth() throws Exception {
        // The reported bug — would get 401 without the fix
        List<StreamedListObjectsResponse> received = new ArrayList<>();

        fga.streamedListObjects(
                        new ClientListObjectsRequest()
                                .user("user:alice")
                                .relation("reader")
                                .type("document"),
                        received::add)
                .get();

        assertEquals(3, received.size());
    }

    @Test
    public void streamingApiExecutor_succeeds_withAuth() throws Exception {
        List<StreamedListObjectsResponse> received = new ArrayList<>();

        ApiExecutorRequestBuilder request =
                ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                        .body(new ListObjectsRequest()
                                .user("user:alice")
                                .relation("reader")
                                .type("document"))
                        .build();

        fga.streamingApiExecutor(StreamedListObjectsResponse.class)
                .stream(request, received::add)
                .get();

        assertEquals(3, received.size());
    }

    @Test
    public void apiExecutor_succeeds_withAuth() throws Exception {
        ApiExecutorRequestBuilder request =
                ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores/{store_id}/list-objects")
                        .body(new ListObjectsRequest()
                                .user("user:alice")
                                .relation("reader")
                                .type("document"))
                        .build();

        ApiResponse<ListObjectsResponse> response =
                fga.apiExecutor().send(request, ListObjectsResponse.class).get();

        assertEquals(200, response.getStatusCode());
        assertEquals(3, response.getData().getObjects().size());
    }
}

