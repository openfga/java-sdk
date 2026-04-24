package dev.openfga.sdk.e2e;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.api.model.*;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End-to-end verification against real Docker containers.
 * NOT a JUnit test — run manually after docker-compose up.
 *
 * Tests:
 *   1. API_TOKEN auth  → preshared-key OpenFGA on port 8082
 *   2. CLIENT_CREDENTIALS auth → OIDC OpenFGA on port 8080 + mock OAuth2 on port 9090
 *
 * For each auth method, exercises:
 *   - CreateStore
 *   - WriteAuthorizationModel
 *   - Write (tuples)
 *   - Check (non-streaming)
 *   - ListObjects (non-streaming)
 *   - StreamedListObjects (streaming) ← the bug from #330
 */
public class E2EStreamingAuthTest {

    // The mock-oauth2-server token endpoint URL.
    // Both the host SDK client and the openfga container reach mock-oauth2 via
    // localhost:9090 (the container uses extra_hosts: localhost → host-gateway).
    // This ensures the JWT "iss" claim matches OpenFGA's configured OIDC issuer.
    private static final String OAUTH2_TOKEN_ENDPOINT = "http://localhost:9090/default/token";
    private static final String OIDC_AUDIENCE = "openfga";

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  E2E Streaming Auth Test — openfga/java-sdk#330 fix     ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        testApiTokenAuth();
        System.out.println();
        testClientCredentialsAuth();

        System.out.println("\n════════════════════════════════════════════════════════════");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("════════════════════════════════════════════════════════════");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // -----------------------------------------------------------------------
    // Test 1: API_TOKEN (preshared key)
    // -----------------------------------------------------------------------
    private static void testApiTokenAuth() throws Exception {
        System.out.println("─── Test Suite: API_TOKEN (preshared key on port 8082) ───");

        ClientConfiguration config = new ClientConfiguration()
                .apiUrl("http://localhost:8082")
                .credentials(new Credentials(new ApiToken("test-api-key-123")));

        OpenFgaClient client = new OpenFgaClient(config);

        // Create store
        var store = client.createStore(new CreateStoreRequest().name("e2e-apitoken-test")).get();
        String storeId = store.getId();
        check("createStore", storeId != null && !storeId.isEmpty());
        System.out.println("       Store ID: " + storeId);

        config.storeId(storeId);
        client = new OpenFgaClient(config);

        // Write auth model
        var model = writeModel(client);
        check("writeAuthModel", model != null);

        // Write tuples
        writeTuples(client);
        check("writeTuples", true);

        // Non-streaming check
        var checkResult = client.check(new ClientCheckRequest()
                        .user("user:anne").relation("reader")._object("document:readme"))
                .get();
        check("check (non-streaming)", checkResult.getAllowed());

        // Non-streaming listObjects
        var listResult = client.listObjects(new ClientListObjectsRequest()
                        .user("user:anne").relation("reader").type("document"))
                .get();
        check("listObjects (non-streaming)", listResult.getObjects().contains("document:readme"));

        // ★ STREAMING listObjects — this is the #330 regression
        List<StreamedListObjectsResponse> streamResults = new ArrayList<>();
        client.streamedListObjects(
                        new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"),
                        streamResults::add)
                .get();
        boolean streamOk = streamResults.stream().anyMatch(r -> "document:readme".equals(r.getObject()));
        check("streamedListObjects (streaming) ★ #330 fix", streamOk);

        System.out.println("       Streamed objects: "
                + streamResults.stream().map(StreamedListObjectsResponse::getObject).toList());
    }

    // -----------------------------------------------------------------------
    // Test 2: CLIENT_CREDENTIALS (OAuth2 OIDC)
    // -----------------------------------------------------------------------
    private static void testClientCredentialsAuth() throws Exception {
        System.out.println("─── Test Suite: CLIENT_CREDENTIALS (OIDC on port 8080) ───");

        // The mock-oauth2-server accepts any client_id/secret for client_credentials grant.
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl("http://localhost:8080")
                .credentials(new Credentials(new ClientCredentials()
                        .clientId("e2e-test-client")
                        .clientSecret("e2e-test-secret")
                        .apiAudience(OIDC_AUDIENCE)
                        .apiTokenIssuer(OAUTH2_TOKEN_ENDPOINT)));

        OpenFgaClient client = new OpenFgaClient(config);

        // Create store
        var store = client.createStore(new CreateStoreRequest().name("e2e-oidc-test")).get();
        String storeId = store.getId();
        check("createStore", storeId != null && !storeId.isEmpty());
        System.out.println("       Store ID: " + storeId);

        config.storeId(storeId);
        client = new OpenFgaClient(config);

        // Write auth model
        var model = writeModel(client);
        check("writeAuthModel", model != null);

        // Write tuples
        writeTuples(client);
        check("writeTuples", true);

        // Non-streaming check
        var checkResult = client.check(new ClientCheckRequest()
                        .user("user:anne").relation("reader")._object("document:readme"))
                .get();
        check("check (non-streaming)", checkResult.getAllowed());

        // Non-streaming listObjects
        var listResult = client.listObjects(new ClientListObjectsRequest()
                        .user("user:anne").relation("reader").type("document"))
                .get();
        check("listObjects (non-streaming)", listResult.getObjects().contains("document:readme"));

        // ★ STREAMING listObjects — the bug from #330
        List<StreamedListObjectsResponse> streamResults = new ArrayList<>();
        client.streamedListObjects(
                        new ClientListObjectsRequest().user("user:anne").relation("reader").type("document"),
                        streamResults::add)
                .get();
        boolean streamOk = streamResults.stream().anyMatch(r -> "document:readme".equals(r.getObject()));
        check("streamedListObjects (streaming) ★ #330 fix", streamOk);

        System.out.println("       Streamed objects: "
                + streamResults.stream().map(StreamedListObjectsResponse::getObject).toList());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static String writeModel(OpenFgaClient client) throws Exception {
        var response = client.writeAuthorizationModel(new WriteAuthorizationModelRequest()
                        .schemaVersion("1.1")
                        .typeDefinitions(List.of(
                                new TypeDefinition().type("user"),
                                new TypeDefinition().type("document").relations(Map.of(
                                        "reader", new Userset()._this(new HashMap<>()),
                                        "writer", new Userset()._this(new HashMap<>())
                                )).metadata(new Metadata().relations(Map.of(
                                        "reader", new RelationMetadata().directlyRelatedUserTypes(
                                                List.of(new RelationReference().type("user"))),
                                        "writer", new RelationMetadata().directlyRelatedUserTypes(
                                                List.of(new RelationReference().type("user")))
                                ))))))
                .get();
        String modelId = response.getAuthorizationModelId();
        System.out.println("       Auth Model ID: " + modelId);
        return modelId;
    }

    private static void writeTuples(OpenFgaClient client) throws Exception {
        client.write(new ClientWriteRequest()
                        .writes(List.of(
                                new ClientTupleKey()
                                        .user("user:anne")
                                        .relation("reader")
                                        ._object("document:readme"),
                                new ClientTupleKey()
                                        .user("user:anne")
                                        .relation("reader")
                                        ._object("document:changelog"),
                                new ClientTupleKey()
                                        .user("user:anne")
                                        .relation("writer")
                                        ._object("document:readme")
                        )), new ClientWriteOptions())
                .get();
        System.out.println("       Wrote 3 tuples");
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.printf("  ✅ %s%n", name);
        } else {
            failed++;
            System.out.printf("  ❌ %s%n", name);
        }
    }
}


