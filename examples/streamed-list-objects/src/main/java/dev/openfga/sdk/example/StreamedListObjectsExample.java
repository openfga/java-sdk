package dev.openfga.sdk.example;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientStreamedListObjectsOptions;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamedListObjectsExample {
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
                System.err.println(
                        "Is OpenFGA server running? Check FGA_API_URL environment variable or default http://localhost:8080");
            } else {
                System.err.println("An error occurred. [" + ex.getClass().getSimpleName() + "]");
            }
            System.exit(1);
        }
    }

    public void run() throws Exception {
        String apiUrl = System.getenv("FGA_API_URL");
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = "http://localhost:8080";
        }

        var configuration = new ClientConfiguration().apiUrl(apiUrl).credentials(new Credentials());

        var client = new OpenFgaClient(configuration);

        System.out.println("Creating temporary store");
        var store = client.createStore(new CreateStoreRequest().name("streamed-list-objects"))
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

        System.out.println("Writing tuples (1000 as owner, 1000 as viewer)");

        // Write in batches of 100 (OpenFGA limit)
        final int batchSize = 100;
        int totalWritten = 0;

        // Write 1000 documents where anne is the owner
        for (int batch = 0; batch < 10; batch++) {
            var tuples = new ArrayList<ClientTupleKey>();
            for (int i = 1; i <= batchSize; i++) {
                tuples.add(new ClientTupleKey()
                        .user("user:anne")
                        .relation("owner")
                        ._object("document:" + (batch * batchSize + i)));
            }
            fga.write(new ClientWriteRequest().writes(tuples)).get();
            totalWritten += tuples.size();
        }

        // Write 1000 documents where anne is a viewer
        for (int batch = 0; batch < 10; batch++) {
            var tuples = new ArrayList<ClientTupleKey>();
            for (int i = 1; i <= batchSize; i++) {
                tuples.add(new ClientTupleKey()
                        .user("user:anne")
                        .relation("viewer")
                        ._object("document:" + (1000 + batch * batchSize + i)));
            }
            fga.write(new ClientWriteRequest().writes(tuples)).get();
            totalWritten += tuples.size();
        }

        System.out.println("Wrote " + totalWritten + " tuples");

        System.out.println("Streaming objects via computed 'can_read' relation...");
        var count = new AtomicInteger(0);

        var request = new ClientListObjectsRequest()
                .user("user:anne")
                .relation("can_read") // Computed: owner OR viewer
                .type("document");

        var options = new ClientStreamedListObjectsOptions().consistency(ConsistencyPreference.HIGHER_CONSISTENCY);

        fga.streamedListObjects(request, options, response -> {
                    int currentCount = count.incrementAndGet();
                    if (currentCount <= 3 || currentCount % 500 == 0) {
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
        // Simplified authorization model demonstrating computed relations
        String modelJson =
                """
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
                        },
                        "can_read": {
                          "union": {
                            "child": [
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "owner"
                                }
                              },
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "viewer"
                                }
                              }
                            ]
                          }
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
                          },
                          "can_read": {
                            "directly_related_user_types": []
                          }
                        }
                      }
                    }
                  ]
                }
                """;

        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.readValue(modelJson, WriteAuthorizationModelRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse authorization model", e);
        }
    }
}
