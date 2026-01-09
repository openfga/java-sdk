package dev.openfga.sdk.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.language.DslToJsonTransformer;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientStreamedListObjectsOptions;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.AuthorizationModel;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamedListObjectsExample {
    // Configuration constants
    private static final String DEFAULT_API_URL = "http://localhost:8080";
    private static final String ENV_API_URL = "FGA_API_URL";
    private static final String STORE_NAME = "streamed-list-objects";

    // Data constants
    private static final String USER_TYPE = "user";
    private static final String DOCUMENT_TYPE = "document";
    private static final String USER_ANNE = "user:anne";
    private static final String RELATION_OWNER = "owner";
    private static final String RELATION_VIEWER = "viewer";
    private static final String RELATION_CAN_READ = "can_read";

    // Batch configuration
    private static final int WRITE_BATCH_SIZE = 100; // OpenFGA limit
    private static final int TOTAL_OWNER_DOCUMENTS = 1000;
    private static final int TOTAL_VIEWER_DOCUMENTS = 1000;
    private static final int VIEWER_DOCUMENT_OFFSET = 1000;

    // Display configuration
    private static final int DISPLAY_FIRST_N = 3;
    private static final int DISPLAY_EVERY_N = 500;

    public static void main(String[] args) {
        try {
            new StreamedListObjectsExample().run();
        } catch (Exception ex) {
            // Avoid logging sensitive data; only display generic info
            if (ex instanceof FgaInvalidParameterException) {
                System.err.println("Validation error in configuration. Please check your configuration for errors.");
            } else if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")
                    || (ex.getCause() != null
                            && ex.getCause().getMessage() != null
                            && ex.getCause().getMessage().contains("Connection refused"))) {
                System.err.println("Is OpenFGA server running? Check " + ENV_API_URL
                        + " environment variable or default " + DEFAULT_API_URL);
            } else {
                System.err.println("An error occurred. [" + ex.getClass().getSimpleName() + "]");
            }
            System.exit(1);
        }
    }

    public void run() throws Exception {
        String apiUrl = System.getenv(ENV_API_URL);
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = DEFAULT_API_URL;
        }

        var configuration = new ClientConfiguration().apiUrl(apiUrl).credentials(new Credentials());

        var client = new OpenFgaClient(configuration);

        System.out.println("Creating temporary store");
        var store = client.createStore(new CreateStoreRequest().name(STORE_NAME))
                .get();

        var clientWithStore = new OpenFgaClient(
                new ClientConfiguration().apiUrl(apiUrl).storeId(store.getId()).credentials(new Credentials()));

        System.out.println("Writing authorization model");
        var authModel = clientWithStore
                .writeAuthorizationModel(createAuthorizationModel())
                .get();

        var fga = new OpenFgaClient(new ClientConfiguration()
                .apiUrl(apiUrl)
                .storeId(store.getId())
                .authorizationModelId(authModel.getAuthorizationModelId())
                .credentials(new Credentials()));

        System.out.println("Writing tuples (" + TOTAL_OWNER_DOCUMENTS + " as owner, " + TOTAL_VIEWER_DOCUMENTS
                + " as viewer)");

        int totalWritten = 0;

        // Write documents where anne is the owner
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

        // Write documents where anne is a viewer
        int viewerBatches = TOTAL_VIEWER_DOCUMENTS / WRITE_BATCH_SIZE;
        for (int batch = 0; batch < viewerBatches; batch++) {
            var tuples = new ArrayList<ClientTupleKey>();
            for (int i = 1; i <= WRITE_BATCH_SIZE; i++) {
                tuples.add(new ClientTupleKey()
                        .user(USER_ANNE)
                        .relation(RELATION_VIEWER)
                        ._object(DOCUMENT_TYPE + ":" + (VIEWER_DOCUMENT_OFFSET + batch * WRITE_BATCH_SIZE + i)));
            }
            fga.write(new ClientWriteRequest().writes(tuples)).get();
            totalWritten += tuples.size();
        }

        System.out.println("Wrote " + totalWritten + " tuples");

        System.out.println("Streaming objects via computed '" + RELATION_CAN_READ + "' relation...");
        var count = new AtomicInteger(0);

        var request = new ClientListObjectsRequest()
                .user(USER_ANNE)
                .relation(RELATION_CAN_READ) // Computed: owner OR viewer
                .type(DOCUMENT_TYPE);

        var options = new ClientStreamedListObjectsOptions().consistency(ConsistencyPreference.HIGHER_CONSISTENCY);

        fga.streamedListObjects(request, options, response -> {
                    int currentCount = count.incrementAndGet();
                    if (currentCount <= DISPLAY_FIRST_N || currentCount % DISPLAY_EVERY_N == 0) {
                        System.out.println("- " + response.getObject());
                    }
                })
                .get();

        System.out.println("✓ Streamed " + count.get() + " objects");

        System.out.println("Cleaning up...");
        fga.deleteStore().get();
        System.out.println("Done");
    }

    private WriteAuthorizationModelRequest createAuthorizationModel() {
        // Define the authorization model using OpenFGA DSL
        // This is much cleaner and more readable than building the model with Java objects
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
            // Transform DSL to JSON and parse into AuthorizationModel
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
