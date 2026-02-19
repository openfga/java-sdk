package dev.openfga.sdk.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.language.DslToJsonTransformer;
import dev.openfga.sdk.api.client.ApiExecutorRequestBuilder;
import dev.openfga.sdk.api.client.HttpMethod;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.AuthorizationModel;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.ListObjectsRequest;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example demonstrating {@link dev.openfga.sdk.api.client.StreamingApiExecutor} usage.
 *
 * <p>This example calls the {@code /stores/{store_id}/streamed-list-objects} OpenFGA endpoint
 * using the generic {@code StreamingApiExecutor} API rather than the typed
 * {@code client.streamedListObjects()} convenience method. It shows how to access any
 * streaming endpoint — including those not yet surfaced as SDK client methods — in the
 * same way that {@link dev.openfga.sdk.api.client.ApiExecutor} does for non-streaming
 * endpoints.</p>
 *
 * <p>Concretely the example:</p>
 * <ol>
 *   <li>Creates a temporary store and writes a simple authorization model.</li>
 *   <li>Writes 200 relationship tuples (100 owners + 100 viewers).</li>
 *   <li>Calls {@code /stores/{store_id}/streamed-list-objects} via
 *       {@code client.streamingApiExecutor(StreamedListObjectsResponse.class).stream(request, consumer)}
 *       to stream all 200 objects back.</li>
 *   <li>Cleans up the store.</li>
 * </ol>
 */
public class StreamingApiExecutorExample {

    private static final String DEFAULT_API_URL = "http://localhost:8080";
    private static final String ENV_API_URL = "FGA_API_URL";
    private static final String STORE_NAME = "streaming-api-executor-example";

    private static final String USER_TYPE = "user";
    private static final String DOCUMENT_TYPE = "document";
    private static final String USER_ANNE = "user:anne";
    private static final String RELATION_OWNER = "owner";
    private static final String RELATION_VIEWER = "viewer";
    private static final String RELATION_CAN_READ = "can_read";

    private static final int WRITE_BATCH_SIZE = 100;
    private static final int TOTAL_OWNER_DOCUMENTS = 100;
    private static final int TOTAL_VIEWER_DOCUMENTS = 100;
    private static final int VIEWER_DOCUMENT_OFFSET = 100;


    public static void main(String[] args) {
        try {
            new StreamingApiExecutorExample().run();
        } catch (Exception ex) {
            if (ex instanceof FgaInvalidParameterException) {
                System.err.println("Validation error in configuration. Please check your configuration for errors.");
            } else if ((ex.getMessage() != null && ex.getMessage().contains("Connection refused"))
                    || (ex.getCause() != null
                            && ex.getCause().getMessage() != null
                            && ex.getCause().getMessage().contains("Connection refused"))) {
                System.err.println("Is OpenFGA server running? Check " + ENV_API_URL
                        + " environment variable or default " + DEFAULT_API_URL);
            } else {
                System.err.println("An error occurred. [" + ex.getClass().getSimpleName() + ": " + ex.getMessage()
                        + "]");
            }
            System.exit(1);
        }
    }

    public void run() throws Exception {
        String apiUrl = System.getenv(ENV_API_URL);
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = DEFAULT_API_URL;
        }

        // ------------------------------------------------------------------ //
        // 1. Bootstrap — create store, write model, write tuples              //
        // ------------------------------------------------------------------ //

        var bootstrapConfig = new ClientConfiguration().apiUrl(apiUrl).credentials(new Credentials());
        var bootstrapClient = new OpenFgaClient(bootstrapConfig);

        System.out.println("Creating temporary store...");
        var store = bootstrapClient
                .createStore(new CreateStoreRequest().name(STORE_NAME))
                .get();
        String storeId = store.getId();
        System.out.println("  Store ID: " + storeId);

        var storeConfig = new ClientConfiguration()
                .apiUrl(apiUrl)
                .storeId(storeId)
                .credentials(new Credentials());
        var storeClient = new OpenFgaClient(storeConfig);

        System.out.println("Writing authorization model...");
        var authModel =
                storeClient.writeAuthorizationModel(createAuthorizationModel()).get();
        String authModelId = authModel.getAuthorizationModelId();
        System.out.println("  Authorization model ID: " + authModelId);

        var fga = new OpenFgaClient(new ClientConfiguration()
                .apiUrl(apiUrl)
                .storeId(storeId)
                .authorizationModelId(authModelId)
                .credentials(new Credentials()));

        System.out.println("Writing tuples...");
        int totalWritten = 0;

        // 100 documents where anne is owner
        int ownerBatches = TOTAL_OWNER_DOCUMENTS / WRITE_BATCH_SIZE;
        for (int batch = 0; batch < ownerBatches; batch++) {
            var tuples = new ArrayList<ClientTupleKey>();
            for (int i = 1; i <= WRITE_BATCH_SIZE; i++) {
                tuples.add(new ClientTupleKey()
                        .user(USER_ANNE)
                        .relation(RELATION_OWNER)
                        ._object(DOCUMENT_TYPE + ":" + (batch * WRITE_BATCH_SIZE + i)));
            }
            fga.write(new ClientWriteRequest().writes(tuples)).get();
            totalWritten += tuples.size();
        }

        // 100 documents where anne is viewer
        int viewerBatches = TOTAL_VIEWER_DOCUMENTS / WRITE_BATCH_SIZE;
        for (int batch = 0; batch < viewerBatches; batch++) {
            var tuples = new ArrayList<ClientTupleKey>();
            for (int i = 1; i <= WRITE_BATCH_SIZE; i++) {
                tuples.add(new ClientTupleKey()
                        .user(USER_ANNE)
                        .relation(RELATION_VIEWER)
                        ._object(DOCUMENT_TYPE
                                + ":"
                                + (VIEWER_DOCUMENT_OFFSET + batch * WRITE_BATCH_SIZE + i)));
            }
            fga.write(new ClientWriteRequest().writes(tuples)).get();
            totalWritten += tuples.size();
        }

        System.out.println("  Wrote " + totalWritten + " tuples");

        // ------------------------------------------------------------------ //
        // 2. Call the streaming endpoint via StreamingApiExecutor             //
        // ------------------------------------------------------------------ //

        System.out.println("\nStreaming objects via StreamingApiExecutor...");
        System.out.println(
                "  Endpoint: POST /stores/{store_id}/streamed-list-objects  (relation: " + RELATION_CAN_READ + ")");

        // Build the request body — same as for the typed streamedListObjects method
        ListObjectsRequest body = new ListObjectsRequest()
                .user(USER_ANNE)
                .relation(RELATION_CAN_READ)
                .type(DOCUMENT_TYPE)
                .authorizationModelId(authModelId);

        // Build the raw HTTP request via ApiExecutorRequestBuilder.
        // Note: {store_id} will be auto-substituted from the client configuration.
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        HttpMethod.POST, "/stores/{store_id}/streamed-list-objects")
                .body(body)
                .build();

        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        fga.streamingApiExecutor(StreamedListObjectsResponse.class)
                .stream(
                        request,
                        // Consumer: called once per streamed JSON object
                        response -> {
                            int n = count.incrementAndGet();
                            if (n <= 3 || n % 50 == 0) {
                                System.out.println("  [" + n + "] " + response.getObject());
                            }
                        },
                        // Error consumer: called for stream-level errors
                        err -> {
                            errorCount.incrementAndGet();
                            System.err.println("  Stream error: " + err.getMessage());
                        })
                .get(); // block until stream is exhausted

        System.out.println("\n✓ Streamed " + count.get() + " objects via StreamingApiExecutor");
        if (errorCount.get() > 0) {
            System.out.println("  (with " + errorCount.get() + " stream-level error(s))");
        }

        // ------------------------------------------------------------------ //
        // 3. Clean up                                                          //
        // ------------------------------------------------------------------ //

        System.out.println("\nCleaning up...");
        fga.deleteStore().get();
        System.out.println("Done");
    }

    private WriteAuthorizationModelRequest createAuthorizationModel() {
        var dslModel = String.format(
                """
                model
                  schema 1.1

                type %s

                type %s
                  relations
                    define %s: [%s]
                    define %s: [%s]
                    define %s: %s or %s
                """,
                USER_TYPE,
                DOCUMENT_TYPE,
                RELATION_OWNER,
                USER_TYPE,
                RELATION_VIEWER,
                USER_TYPE,
                RELATION_CAN_READ,
                RELATION_OWNER,
                RELATION_VIEWER);

        try {
            var jsonModel = new DslToJsonTransformer().transform(dslModel);
            var mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            var authModel = mapper.readValue(jsonModel, AuthorizationModel.class);
            return new WriteAuthorizationModelRequest()
                    .typeDefinitions(authModel.getTypeDefinitions())
                    .schemaVersion(authModel.getSchemaVersion())
                    .conditions(authModel.getConditions());
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform DSL model to JSON", e);
        }
    }
}

