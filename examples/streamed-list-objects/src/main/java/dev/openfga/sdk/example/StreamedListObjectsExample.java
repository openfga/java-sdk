package dev.openfga.sdk.example;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamedListObjectsExample {
    public static void main(String[] args) throws Exception {
        new StreamedListObjectsExample().run();
    }

    public void run() throws Exception {
        // Configure the client
        var credentials = new Credentials();
        if (System.getenv("FGA_CLIENT_ID") != null) {
            credentials = new Credentials(new ClientCredentials()
                    .apiAudience(System.getenv("FGA_API_AUDIENCE"))
                    .apiTokenIssuer(System.getenv("FGA_API_TOKEN_ISSUER"))
                    .clientId(System.getenv("FGA_CLIENT_ID"))
                    .clientSecret(System.getenv("FGA_CLIENT_SECRET")));
        } else {
            System.out.println("Proceeding with no credentials (expecting localhost)");
        }

        String apiUrl = System.getenv("FGA_API_URL");
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = "http://localhost:8080";
        }

        var configuration = new ClientConfiguration().apiUrl(apiUrl).credentials(credentials);

        var fgaClient = new OpenFgaClient(configuration);

        // Create a temporary store
        var store = fgaClient
                .createStore(new CreateStoreRequest().name("Test Store"))
                .get();
        String storeId = store.getId();
        System.out.println("Created temporary store (" + storeId + ")");
        fgaClient.setStoreId(storeId);

        // Create an authorization model
        var authModel = createAuthorizationModel();
        var model = fgaClient.writeAuthorizationModel(authModel).get();
        String modelId = model.getAuthorizationModelId();
        System.out.println("Created temporary authorization model (" + modelId + ")");

        // Write 100 mock tuples
        System.out.println("Writing 100 mock tuples to store.");
        List<ClientTupleKey> writes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            writes.add(new ClientTupleKey().user("user:anne").relation("owner")._object("document:" + i));
        }

        fgaClient.write(new ClientWriteRequest().writes(writes)).get();

        // Stream objects
        System.out.println("Listing objects using streaming endpoint:");
        List<String> results = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);

        ClientListObjectsRequest request = new ClientListObjectsRequest()
                .type("document")
                .relation("owner")
                .user("user:anne");

        fgaClient
                .streamedListObjects(request, object -> {
                    System.out.println("  " + object);
                    results.add(object);
                    count.incrementAndGet();
                })
                .thenRun(() -> {
                    System.out.println("Streaming complete!");
                    System.out.println("API returned " + results.size() + " objects.");
                })
                .get(); // Wait for completion

        System.out.println("All results processed.");

        // Clean up - delete the temporary store
        try {
            fgaClient.deleteStore().get();
            System.out.println("Deleted temporary store (" + storeId + ")");
        } catch (Exception e) {
            System.err.println("Failed to delete store: " + e.getMessage());
        }

        System.out.println("Finished.");
    }

    private WriteAuthorizationModelRequest createAuthorizationModel() {
        // This is a simplified authorization model for the example
        // In a real application, you would load this from a file or define it more comprehensively
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
                      "type": "group",
                      "relations": {
                        "member": {
                          "this": {}
                        }
                      },
                      "metadata": {
                        "relations": {
                          "member": {
                            "directly_related_user_types": [
                              {"type": "user"}
                            ]
                          }
                        }
                      }
                    },
                    {
                      "type": "folder",
                      "relations": {
                        "can_create_file": {
                          "computedUserset": {
                            "object": "",
                            "relation": "owner"
                          }
                        },
                        "owner": {
                          "this": {}
                        },
                        "parent": {
                          "this": {}
                        },
                        "viewer": {
                          "union": {
                            "child": [
                              {
                                "this": {}
                              },
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "owner"
                                }
                              },
                              {
                                "tupleToUserset": {
                                  "tupleset": {
                                    "object": "",
                                    "relation": "parent"
                                  },
                                  "computedUserset": {
                                    "object": "",
                                    "relation": "viewer"
                                  }
                                }
                              }
                            ]
                          }
                        }
                      },
                      "metadata": {
                        "relations": {
                          "can_create_file": {
                            "directly_related_user_types": []
                          },
                          "owner": {
                            "directly_related_user_types": [
                              {"type": "user"}
                            ]
                          },
                          "parent": {
                            "directly_related_user_types": [
                              {"type": "folder"}
                            ]
                          },
                          "viewer": {
                            "directly_related_user_types": [
                              {"type": "user"},
                              {"type": "user", "wildcard": {}},
                              {"type": "group", "relation": "member"}
                            ]
                          }
                        }
                      }
                    },
                    {
                      "type": "document",
                      "relations": {
                        "can_change_owner": {
                          "computedUserset": {
                            "object": "",
                            "relation": "owner"
                          }
                        },
                        "owner": {
                          "this": {}
                        },
                        "parent": {
                          "this": {}
                        },
                        "can_read": {
                          "union": {
                            "child": [
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "viewer"
                                }
                              },
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "owner"
                                }
                              },
                              {
                                "tupleToUserset": {
                                  "tupleset": {
                                    "object": "",
                                    "relation": "parent"
                                  },
                                  "computedUserset": {
                                    "object": "",
                                    "relation": "viewer"
                                  }
                                }
                              }
                            ]
                          }
                        },
                        "can_share": {
                          "union": {
                            "child": [
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "owner"
                                }
                              },
                              {
                                "tupleToUserset": {
                                  "tupleset": {
                                    "object": "",
                                    "relation": "parent"
                                  },
                                  "computedUserset": {
                                    "object": "",
                                    "relation": "owner"
                                  }
                                }
                              }
                            ]
                          }
                        },
                        "viewer": {
                          "this": {}
                        },
                        "can_write": {
                          "union": {
                            "child": [
                              {
                                "computedUserset": {
                                  "object": "",
                                  "relation": "owner"
                                }
                              },
                              {
                                "tupleToUserset": {
                                  "tupleset": {
                                    "object": "",
                                    "relation": "parent"
                                  },
                                  "computedUserset": {
                                    "object": "",
                                    "relation": "owner"
                                  }
                                }
                              }
                            ]
                          }
                        }
                      },
                      "metadata": {
                        "relations": {
                          "can_change_owner": {
                            "directly_related_user_types": []
                          },
                          "owner": {
                            "directly_related_user_types": [
                              {"type": "user"}
                            ]
                          },
                          "parent": {
                            "directly_related_user_types": [
                              {"type": "folder"}
                            ]
                          },
                          "can_read": {
                            "directly_related_user_types": []
                          },
                          "can_share": {
                            "directly_related_user_types": []
                          },
                          "viewer": {
                            "directly_related_user_types": [
                              {"type": "user"},
                              {"type": "user", "wildcard": {}},
                              {"type": "group", "relation": "member"}
                            ]
                          },
                          "can_write": {
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