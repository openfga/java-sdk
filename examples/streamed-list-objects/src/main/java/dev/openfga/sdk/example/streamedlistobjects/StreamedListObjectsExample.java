package dev.openfga.sdk.example.streamedlistobjects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.api.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example demonstrating the usage of the Streamed ListObjects API.
 *
 * The Streamed ListObjects API returns results as they are computed, rather than
 * collecting all results before returning. This is particularly useful for:
 * - Large result sets that would take a long time to collect
 * - Scenarios where you want to start processing results immediately
 * - Cases where you might not need all results (early termination)
 */
public class StreamedListObjectsExample {
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

        // Create a test store
        System.out.println("Creating test store...");
        var store =
                fgaClient.createStore(new CreateStoreRequest().name("StreamedListObjects Test Store"))
                        .get();
        fgaClient.setStoreId(store.getId());
        System.out.println("Created store: " + store.getId());

        // Create an authorization model
        System.out.println("Creating authorization model...");
        String authModelJson = """
                {
                  "schema_version": "1.1",
                  "type_definitions": [
                    {
                      "type": "user",
                      "relations": {}
                    },
                    {
                      "type": "document",
                      "relations": {
                        "owner": {
                          "this": {}
                        },
                        "viewer": {
                          "this": {}
                        }
                      },
                      "metadata": {
                        "relations": {
                          "owner": {
                            "directly_related_user_types": [
                              {"type": "user"}
                            ]
                          },
                          "viewer": {
                            "directly_related_user_types": [
                              {"type": "user"}
                            ]
                          }
                        }
                      }
                    }
                  ]
                }
                """;

        var mapper = new ObjectMapper();
        var authModel = mapper.readValue(authModelJson, new TypeReference<WriteAuthorizationModelRequest>() {});
        var modelResponse = fgaClient.writeAuthorizationModel(authModel).get();
        fgaClient.setAuthorizationModelId(modelResponse.getAuthorizationModelId());
        System.out.println("Created model: " + modelResponse.getAuthorizationModelId());

        // Write some test tuples
        System.out.println("\nWriting 100 test tuples...");
        List<ClientTuple> writes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            writes.add(new ClientTuple()
                    .user("user:anne")
                    .relation("owner")
                    ._object("document:" + i));
        }

        fgaClient
                .write(new ClientWriteRequest().writes(writes))
                .get();
        System.out.println("Successfully wrote 100 tuples");

        // Example 1: Use streamedListObjects to get all objects
        System.out.println("\n=== Example 1: List all objects using streaming ===");
        var request1 = new ClientListObjectsRequest()
                .type("document")
                .relation("owner")
                .user("user:anne");

        var objectStream1 = fgaClient.streamedListObjects(request1).get();
        List<String> allObjects =
                objectStream1.map(StreamedListObjectsResponse::getObject).collect(Collectors.toList());

        System.out.println("Total objects found: " + allObjects.size());
        System.out.println("First 5 objects: " + allObjects.subList(0, Math.min(5, allObjects.size())));

        // Example 2: Early termination - stop after finding first N objects
        System.out.println("\n=== Example 2: Early termination (first 10 objects) ===");
        var request2 = new ClientListObjectsRequest()
                .type("document")
                .relation("owner")
                .user("user:anne");

        var objectStream2 = fgaClient.streamedListObjects(request2).get();
        List<String> firstTen = objectStream2
                .map(StreamedListObjectsResponse::getObject)
                .limit(10)
                .collect(Collectors.toList());

        System.out.println("First 10 objects: " + firstTen);

        // Example 3: Process objects as they arrive (immediate processing)
        System.out.println("\n=== Example 3: Process objects immediately ===");
        var request3 = new ClientListObjectsRequest()
                .type("document")
                .relation("owner")
                .user("user:anne");

        var objectStream3 = fgaClient.streamedListObjects(request3).get();
        objectStream3
                .map(StreamedListObjectsResponse::getObject)
                .limit(10)
                .forEach(obj -> {
                    // Process each object as it arrives
                    System.out.println("  Processing: " + obj);
                });

        // Example 4: With options (custom authorization model ID and consistency preference)
        System.out.println("\n=== Example 4: With options ===");
        var request4 = new ClientListObjectsRequest()
                .type("document")
                .relation("owner")
                .user("user:anne");

        var options4 = new ClientListObjectsOptions().consistency(ConsistencyPreference.HIGHER_CONSISTENCY);

        var objectStream4 = fgaClient.streamedListObjects(request4, options4).get();
        long count = objectStream4.count();
        System.out.println("Total objects with higher consistency: " + count);

        // Clean up - delete the test store
        System.out.println("\n=== Cleaning up ===");
        fgaClient.deleteStore().get();
        System.out.println("Deleted test store: " + store.getId());

        System.out.println("\nExample completed successfully!");
    }
}