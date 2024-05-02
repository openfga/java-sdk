package dev.openfga.sdk.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.ClientAssertion;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.api.model.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class Example1 {
    public void run(String apiUrl) throws Exception {
        var credentials = new Credentials();
        if (System.getenv("FGA_CLIENT_ID") != null) {
            credentials = new Credentials(new ClientCredentials()
                    .apiAudience(System.getenv("FGA_API_AUDIENCE"))
                    .apiTokenIssuer(System.getenv("FGA_TOKEN_ISSUER"))
                    .clientId("FGA_CLIENT_ID")
                    .clientSecret("FGA_CLIENT_SECRET"));
        } else {
            System.out.println("Proceeding with no credentials (expecting localhost)");
        }

        var configuration = new ClientConfiguration()
                .apiUrl(apiUrl) // required, e.g. https://api.fga.example
                .storeId(System.getenv("FGA_STORE_ID")) // not needed when calling `CreateStore` or `ListStores`
                .authorizationModelId(
                        System.getenv("FGA_MODEL_ID")) // Optional, can be overridden per request
                .credentials(credentials);
        var fgaClient = new OpenFgaClient(configuration);

        // ListStores
        System.out.println("Listing Stores");
        var stores1 = fgaClient.listStores().get();
        System.out.println("Stores Count: " + stores1.getStores().size());

        // CreateStore
        System.out.println("Creating Test Store");
        var store = fgaClient
                .createStore(new CreateStoreRequest().name("Test Store"))
                .get();
        System.out.println("Test Store ID: " + store.getId());

        // Set the store id
        fgaClient.setStoreId(store.getId());

        // ListStores after Create
        System.out.println("Listing Stores");
        var stores = fgaClient.listStores().get();
        System.out.println("Stores Count: " + stores.getStores().size());

        // GetStore
        System.out.println("Getting Current Store");
        var currentStore = fgaClient.getStore().get();
        System.out.println("Current Store Name: " + currentStore.getName());

        // ReadAuthorizationModels
        System.out.println("Reading Authorization Models");
        var models = fgaClient.readAuthorizationModels().get();
        System.out.println("Models Count: " + models.getAuthorizationModels().size());

        // ReadLatestAuthorizationModel
        try {
            var latestAuthorizationModel = fgaClient
                    .readLatestAuthorizationModel()
                    .get(); // TODO: Should this really return null? Optional<...Response>?
            System.out.println("Latest Authorization Model ID "
                    + latestAuthorizationModel.getAuthorizationModel().getId());
        } catch (Exception e) {
            System.out.println("Latest Authorization Model not found");
        }

        var mapper = new ObjectMapper().findAndRegisterModules();

        // WriteAuthorizationModel
        var authModelJson = loadResource("example1-auth-model.json");
        var authorizationModel = fgaClient
                .writeAuthorizationModel(mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class))
                .get();
        System.out.println("Authorization Model ID " + authorizationModel.getAuthorizationModelId());

        // ReadAuthorizationModels - after Write
        System.out.println("Reading Authorization Models");
        models = fgaClient.readAuthorizationModels().get();
        System.out.println("Models Count: " + models.getAuthorizationModels().size());

        // ReadLatestAuthorizationModel - after Write
        var latestAuthorizationModel = fgaClient.readLatestAuthorizationModel().get();
        System.out.println("Latest Authorization Model ID "
                + latestAuthorizationModel.getAuthorizationModel().getId());

        // Set the model ID
        fgaClient.setAuthorizationModelId(
                latestAuthorizationModel.getAuthorizationModel().getId());

        // Write
        System.out.println("Writing Tuples");
        fgaClient
                .write(
                        new ClientWriteRequest()
                                .writes(List.of(new ClientTupleKey()
                                        .user("user:anne")
                                        .relation("writer")
                                        ._object("document:roadmap"))),
                        new ClientWriteOptions()
                                .disableTransactions(true)
                                .authorizationModelId(authorizationModel.getAuthorizationModelId()))
                .get();
        System.out.println("Done Writing Tuples");

        // Read
        System.out.println("Reading Tuples");
        var readTuples = fgaClient.read(new ClientReadRequest()).get();
        System.out.println("Read Tuples" + mapper.writeValueAsString(readTuples));

        // ReadChanges
        System.out.println("Reading Tuple Changess");
        var readChangesTuples =
                fgaClient.readChanges(new ClientReadChangesRequest()).get();
        System.out.println("Read Changes Tuples" + mapper.writeValueAsString(readChangesTuples));

        // Check
        System.out.println("Checking for access");
        try {
            var failingCheckResponse = fgaClient
                    .check(new ClientCheckRequest()
                            .user("user:anne")
                            .relation("reader")
                            ._object("document:roadmap"))
                    .get();
            System.out.println("Allowed: " + failingCheckResponse.getAllowed());
        } catch (Exception e) {
            System.out.println("Failed due to: " + e.getMessage());
        }

        // Checking for access with context
        // TODO: Add ClientCheckRequest.context
        // System.out.println("Checking for access with context");
        // var checkResponse = fgaClient
        //         .check(new ClientCheckRequest()
        //                 .user("user:anne")
        //                 .relation("reader")
        //                 ._object("document:roadmap")
        //                 .context(Map.of("ViewCount", 100)))
        //         .get();
        // System.out.println("Allowed: " + checkResponse.getAllowed());

        // WriteAssertions
        fgaClient
                .writeAssertions(List.of(
                        new ClientAssertion()
                                .user("user:carl")
                                .relation("writer")
                                ._object("document:budget")
                                .expectation(true),
                        new ClientAssertion()
                                .user("user:anne")
                                .relation("reader")
                                ._object("document:roadmap")
                                .expectation(false)))
                .get();
        System.out.println("Assertions updated");

        // ReadAssertions
        System.out.println("Reading Assertions");
        var assertions = fgaClient.readAssertions().get();
        System.out.println("Assertions " + mapper.writeValueAsString(assertions));

        // DeleteStore
        System.out.println("Deleting Current Store");
        fgaClient.deleteStore().get();
        System.out.println("Deleted Store: " + currentStore.getName());
    }

    public static void main(String[] args) {
        System.out.println("=== Example 1 (Java) ===");
        try {
            new Example1().run(System.getenv("FGA_API_URL"));
        } catch (Exception e) {
            System.err.printf("ERROR: %s%n", e);
        }
    }

    String module = "main"; // Used only for integration testing

    // Small helper function to load resource files relative to this class.
    private String loadResource(String filename) {
        try {
            var filepath = Paths.get("src", module, "resources", filename);
            return Files.readString(filepath, StandardCharsets.UTF_8);
        } catch (IOException cause) {
            throw new RuntimeException("Unable to load resource: " + filename, cause);
        }
    }
}
