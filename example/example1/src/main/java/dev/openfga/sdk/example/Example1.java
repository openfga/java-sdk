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

        /*
         var config = new ClientConfiguration()
                .apiUrl("https://api.us1.fga.dev")
                .storeId("01HFS372PCPRMV4FC5H3H38VNR")
                .authorizationModelId("01HMVXP2R3W1BZ4PTDZY4ZYTZA")
                .credentials(new Credentials(
                    new ClientCredentials()
                            .apiTokenIssuer("auth.fga.dev")
                            .apiAudience("https://api.us1.fga.dev/")
                            .clientId("ps4iOs4fM64rpZgcx6WYDLGWNG9b6dYN")
                            .clientSecret("XpBAmtftq4kLRQ7eTmho7upAk6-ZHuviFfVVH5_S2ABRgoSv9p7W7bHcxQvh7FAC")
                ));
         */
        var credentials = new Credentials();
//        if (System.getenv("FGA_CLIENT_ID") != null) {
            credentials = new Credentials(new ClientCredentials()
                    .apiAudience("https://api.us1.fga.dev/")
                    .apiTokenIssuer("auth.fga.dev")
                    .clientId("ps4iOs4fM64rpZgcx6WYDLGWNG9b6dYN")
                    .clientSecret("XpBAmtftq4kLRQ7eTmho7upAk6-ZHuviFfVVH5_S2ABRgoSv9p7W7bHcxQvh7FAC"));
//        } else {
//            System.out.println("Proceeding with no credentials (expecting localhost)");
//        }

        var configuration = new ClientConfiguration()
                .apiUrl("https://api.us1.fga.dev") // required, e.g. https://api.fga.example
                .storeId("01HFS372PCPRMV4FC5H3H38VNR") // not needed when calling `CreateStore` or `ListStores`
                .authorizationModelId("01HMVXP2R3W1BZ4PTDZY4ZYTZA") // Optional, can be overridden per request
                .credentials(credentials);
        var fgaClient = new OpenFgaClient(configuration);

        // ListStores
//        System.out.println("Listing Stores");
//        var stores1 = fgaClient.listStores().get();
//        System.out.println("Stores Count: " + stores1.getStores().size());

        // CreateStore
//        System.out.println("Creating Test Store");
//        var store = fgaClient
//                .createStore(new CreateStoreRequest().name("Test Store"))
//                .get();
//        System.out.println("Test Store ID: " + store.getId());
//
//        // Set the store id
//        fgaClient.setStoreId(store.getId());
//
//        // ListStores after Create
//        System.out.println("Listing Stores");
//        var stores = fgaClient.listStores().get();
//        System.out.println("Stores Count: " + stores.getStores().size());
//
//        // GetStore
//        System.out.println("Getting Current Store");
//        var currentStore = fgaClient.getStore().get();
//        System.out.println("Current Store Name: " + currentStore.getName());
//
//        // ReadAuthorizationModels
//        System.out.println("Reading Authorization Models");
//        var models = fgaClient.readAuthorizationModels().get();
//        System.out.println("Models Count: " + models.getAuthorizationModels().size());
//
//        // ReadLatestAuthorizationModel
//        try {
//            var latestAuthorizationModel = fgaClient
//                    .readLatestAuthorizationModel()
//                    .get(); // TODO: Should this really return null? Optional<...Response>?
//            System.out.println("Latest Authorization Model ID "
//                    + latestAuthorizationModel.getAuthorizationModel().getId());
//        } catch (Exception e) {
//            System.out.println("Latest Authorization Model not found");
//        }
//
//        var mapper = new ObjectMapper().findAndRegisterModules();
//
//        // WriteAuthorizationModel
//        var authModelJson = loadResource("example1-auth-model.json");
//        var authorizationModel = fgaClient
//                .writeAuthorizationModel(mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class))
//                .get();
//        System.out.println("Authorization Model ID " + authorizationModel.getAuthorizationModelId());
//
//        // ReadAuthorizationModels - after Write
//        System.out.println("Reading Authorization Models");
//        models = fgaClient.readAuthorizationModels().get();
//        System.out.println("Models Count: " + models.getAuthorizationModels().size());
//
//        // ReadLatestAuthorizationModel - after Write
//        var latestAuthorizationModel = fgaClient.readLatestAuthorizationModel().get();
//        System.out.println("Latest Authorization Model ID "
//                + latestAuthorizationModel.getAuthorizationModel().getId());
//
//        // Set the model ID
//        fgaClient.setAuthorizationModelId(
//                latestAuthorizationModel.getAuthorizationModel().getId());
//
//        // Write
//        System.out.println("Writing Tuples");
//        fgaClient
//                .write(
//                        new ClientWriteRequest()
//                                .writes(List.of(new ClientTupleKey()
//                                        .user("user:anne")
//                                        .relation("writer")
//                                        ._object("document:0192ab2a-d83f-756d-9397-c5ed9f3cb69a"))),
//                        new ClientWriteOptions()
//                                .disableTransactions(true)
//                                .authorizationModelId(authorizationModel.getAuthorizationModelId()))
//                .get();
//        System.out.println("Done Writing Tuples");
//
//        // Read
//        System.out.println("Reading Tuples");
//        var readTuples = fgaClient.read(new ClientReadRequest()).get();
//        System.out.println("Read Tuples" + mapper.writeValueAsString(readTuples));
//
//        // ReadChanges
//        System.out.println("Reading Tuple Changess");
//        var readChangesTuples =
//                fgaClient.readChanges(new ClientReadChangesRequest()).get();
//        System.out.println("Read Changes Tuples" + mapper.writeValueAsString(readChangesTuples));
//
//        // Check
//        System.out.println("Checking for access");
//        try {
//            var failingCheckResponse = fgaClient
//                    .check(new ClientCheckRequest()
//                            .user("user:anne")
//                            .relation("reader")
//                            ._object("document:0192ab2a-d83f-756d-9397-c5ed9f3cb69a"))
//                    .get();
//            System.out.println("Allowed: " + failingCheckResponse.getAllowed());
//        } catch (Exception e) {
//            System.out.println("Failed due to: " + e.getMessage());
//        }

        System.out.println("Checking for access using batch check");
        ClientBatchCheckItem item1 = new ClientBatchCheckItem()
                .user("user:anne")
                .relation("owner")
                ._object("folder:date-1727455620817")
                .correlationId("cor-1");
        ClientBatchCheckItem item2 = new ClientBatchCheckItem()
                .user("user:anne")
                .relation("owner")
                ._object("folder:date-1727455599535")
                .correlationId("cor-2");
        ClientBatchCheckItem item3 = new ClientBatchCheckItem()
                .user("user:anne")
                .relation("owner")
                ._object("blah:1")
                .correlationId("cor-3");
        try {
            var batchResponse = fgaClient.batchCheck(new ClientBatchCheckRequest().checks(List.of(item1, item2, item3))).get();
            var response1 = batchResponse.getResult().stream().filter(r -> r.getCorrelationId().equals("cor-1")).findFirst().orElse(null);
            System.out.println("Batch Check Folder 1 Allowed: " + response1.isAllowed());
            var response2 = batchResponse.getResult().stream().filter(r -> r.getCorrelationId().equals("cor-2")).findFirst().orElse(null);
            System.out.println("Batch Check Folder 2 Allowed: " + response2.isAllowed());
            var response3 = batchResponse.getResult().stream().filter(r -> r.getCorrelationId().equals("cor-3")).findFirst().orElse(null);
            System.out.println("Batch Check doc Allowed: " + response3.isAllowed());
            System.out.println("Batch Check doc Error: " + response3.getError());
        } catch (Exception e) {
            System.out.println("Failed due to: " + e.getMessage());
        }



//        var batchCheckResponse = fgaClient
//                .clientBatchCheck(new ClientBatchCheckRequest()
//                        .checks(List.of(new ClientCheckRequest()
//                                        .user("user:anne")
//                                        .relation("reader")
//                                        ._object("document:0192ab2a-d83f-756d-9397-c5ed9f3cb69a"),
//                                new ClientCheckRequest()
//                                        .user("user:carl")
//                                        .relation("writer")
//                                        ._object("document:budget"))))
//                .get();
        // Checking for access with context
        // TODO: Add ClientCheckRequest.context
        // System.out.println("Checking for access with context");
        // var checkResponse = fgaClient
        //         .check(new ClientCheckRequest()
        //                 .user("user:anne")
        //                 .relation("reader")
        //                 ._object("document:0192ab2a-d83f-756d-9397-c5ed9f3cb69a")
        //                 .context(Map.of("ViewCount", 100)))
        //         .get();
        // System.out.println("Allowed: " + checkResponse.getAllowed());

        // WriteAssertions
//        fgaClient
//                .writeAssertions(List.of(
//                        new ClientAssertion()
//                                .user("user:carl")
//                                .relation("writer")
//                                ._object("document:budget")
//                                .expectation(true),
//                        new ClientAssertion()
//                                .user("user:anne")
//                                .relation("reader")
//                                ._object("document:0192ab2a-d83f-756d-9397-c5ed9f3cb69a")
//                                .expectation(false)))
//                .get();
//        System.out.println("Assertions updated");
//
//        // ReadAssertions
//        System.out.println("Reading Assertions");
//        var assertions = fgaClient.readAssertions().get();
//        System.out.println("Assertions " + mapper.writeValueAsString(assertions));
//
//        // DeleteStore
//        System.out.println("Deleting Current Store");
//        fgaClient.deleteStore().get();
//        System.out.println("Deleted Store: " + currentStore.getName());
    }

    public static void main(String[] args) {
        System.out.println("=== Example 1 (Java) ===");
        try {
            new Example1().run("https://api.us1.fga.dev");
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
