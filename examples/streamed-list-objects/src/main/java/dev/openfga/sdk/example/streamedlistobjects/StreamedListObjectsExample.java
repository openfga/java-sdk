package dev.openfga.sdk.example.streamedlistobjects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.api.model.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example demonstrating the usage of the Streamed ListObjects API.
 *
 * <p>This example demonstrates working with OpenFGA's `/streamed-list-objects` endpoint using the
 * Java SDK's `streamedListObjects()` method.
 *
 * <p>The Streamed ListObjects API returns results as they are computed, rather than collecting all
 * results before returning. This is particularly useful for:
 *
 * <ul>
 *   <li>Large result sets that would take a long time to collect
 *   <li>Scenarios where you want to start processing results immediately
 *   <li>Cases where you might not need all results (early termination)
 * </ul>
 */
public class StreamedListObjectsExample {
    private static final int TUPLE_COUNT = 2000;

    public static void main(String[] args) throws Exception {
        // Configure the SDK
        var credentials = new Credentials();
        if (System.getenv("FGA_CLIENT_ID") != null) {
            credentials = new Credentials(new ClientCredentials()
                    .apiAudience(System.getenv("FGA_API_AUDIENCE"))
                    .apiTokenIssuer(System.getenv("FGA_API_TOKEN_ISSUER"))
                    .clientId(System.getenv("FGA_CLIENT_ID"))
                    .clientSecret(System.getenv("FGA_CLIENT_SECRET")));
        }

        var configuration = new ClientConfiguration()
                .apiUrl(System.getenv("FGA_API_URL")) // e.g., http://localhost:8080
                .credentials(credentials);

        var fgaClient = new OpenFgaClient(configuration);

        // Create our temporary store
        String storeId = createStore(fgaClient);
        System.out.println("Created temporary store (" + storeId + ")");

        // Configure the SDK to use the temporary store for the rest of the example
        fgaClient.setStoreId(storeId);

        // Load the authorization model from a file and write it to the server
        String modelId = writeModel(fgaClient);
        System.out.println("Created temporary authorization model (" + modelId + ")\n");

        // Configure the SDK to use this authorization model for the rest of the example
        fgaClient.setAuthorizationModelId(modelId);

        // Write a bunch of example tuples to the temporary store
        int wrote = writeTuples(fgaClient, TUPLE_COUNT);
        System.out.println("Wrote " + wrote + " tuples to the store.\n");

        ////////////////////////////////
        // Demonstrate streaming vs standard list objects

        // Craft a request to list all `documents` owned by `user:anne`
        var request = new ClientListObjectsRequest()
                .type("document")
                .relation("owner")
                .user("user:anne");

        // Send a single request to the server using both the streamed and standard endpoints
        List<String> streamedResults = streamedListObjects(fgaClient, request);
        List<String> standardResults = listObjects(fgaClient, request);

        System.out.println(
                "/streamed-list-objects returned " + streamedResults.size() + " objects in a single request.");

        System.out.println("/list-objects returned " + standardResults.size() + " objects in a single request.");

        ////////////////////////////////
        // Clean up - delete the test store
        fgaClient.deleteStore().get();
        System.out.println("\nDeleted temporary store (" + storeId + ")");
    }

    /**
     * Create a temporary store. The store will be deleted at the end of the example.
     */
    private static String createStore(OpenFgaClient fgaClient) throws Exception {
        var response = fgaClient
                .createStore(new CreateStoreRequest().name("Demo Store"))
                .get();
        return response.getId();
    }

    /**
     * Load the authorization model from a file and write it to the server.
     */
    private static String writeModel(OpenFgaClient fgaClient) throws Exception {
        var mapper = new ObjectMapper();
        var modelFile = new File("model.json");
        var authModel = mapper.readValue(modelFile, new TypeReference<WriteAuthorizationModelRequest>() {});
        var response = fgaClient.writeAuthorizationModel(authModel).get();
        return response.getAuthorizationModelId();
    }

    /**
     * Write a variable number of tuples to the temporary store.
     */
    private static int writeTuples(OpenFgaClient fgaClient, int quantity) throws Exception {
        int chunks = quantity / 100;

        for (int chunk = 0; chunk < chunks; chunk++) {
            List<ClientTupleKey> writes = new ArrayList<>();
            for (int t = 0; t < 100; t++) {
                writes.add(new ClientTupleKey()
                        .user("user:anne")
                        .relation("owner")
                        ._object("document:" + (chunk * 100 + t)));
            }
            fgaClient.write(new ClientWriteRequest().writes(writes)).get();
        }

        return quantity;
    }

    /**
     * Send our request to the streaming endpoint, and collect all results.
     *
     * <p>Note that streamedListObjects() returns a Stream, so we could process results as they
     * come in. For the sake of this example, we'll just collect all the results into a list and
     * return them all at once.
     */
    private static List<String> streamedListObjects(OpenFgaClient fgaClient, ClientListObjectsRequest request)
            throws Exception {
        try (var objectStream = fgaClient.streamedListObjects(request).get()) {
            return objectStream.map(StreamedListObjectsResponse::getObject).collect(Collectors.toList());
        }
    }

    /**
     * For comparison sake, here is the non-streamed version of the same call, using
     * listObjects().
     *
     * <p>Note that in the non-streamed version, the server will return a maximum of 1000 results.
     */
    private static List<String> listObjects(OpenFgaClient fgaClient, ClientListObjectsRequest request)
            throws Exception {
        var response = fgaClient.listObjects(request).get();
        return response.getObjects();
    }
}