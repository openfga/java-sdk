package dev.openfga.sdk.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.*;

import java.nio.file.Files;
import java.nio.file.Paths;

class Example1 {

    public static void main(String[] args) throws Exception {
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
                .apiUrl(System.getenv("FGA_API_URL")) // required, e.g. https://api.fga.example
                .storeId(System.getenv("FGA_STORE_ID")) // not needed when calling `CreateStore` or `ListStores`
                .authorizationModelId(
                        System.getenv("FGA_AUTHORIZATION_MODEL_ID")) // Optional, can be overridden per request
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
        var authModelJson = Files.readString(Paths.get("src", "main", "resources", "auth-model.json"));
        var authorizationModel = fgaClient.writeAuthorizationModel(mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class)).get();
        System.out.println("Authorization Model ID " + authorizationModel.getAuthorizationModelId());
        
        // ReadAuthorizationModels - after Write
        System.out.println("Reading Authorization Models");
        models = fgaClient.readAuthorizationModels().get();
        System.out.println("Models Count: " + models.getAuthorizationModels().size());

        // ReadLatestAuthorizationModel - after Write
        var latestAuthorizationModel = fgaClient.readLatestAuthorizationModel().get();
        System.out.println("Latest Authorization Model ID " + latestAuthorizationModel.getAuthorizationModel().getId());

        // Set the model ID
        fgaClient.setAuthorizationModelId(latestAuthorizationModel.getAuthorizationModel().getId());

        System.out.println("MORE COMING SOON");
    }
}
