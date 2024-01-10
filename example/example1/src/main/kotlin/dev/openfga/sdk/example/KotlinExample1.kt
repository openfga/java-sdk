package dev.openfga.sdk.example

import com.fasterxml.jackson.databind.ObjectMapper
import dev.openfga.sdk.api.client.ClientAssertion
import dev.openfga.sdk.api.client.OpenFgaClient
import dev.openfga.sdk.api.client.model.*
import dev.openfga.sdk.api.configuration.ClientConfiguration
import dev.openfga.sdk.api.configuration.ClientCredentials
import dev.openfga.sdk.api.configuration.ClientWriteOptions
import dev.openfga.sdk.api.configuration.Credentials
import dev.openfga.sdk.api.model.CreateStoreRequest
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest

internal class KotlinExample1 {
    @Throws(Exception::class)
    fun run() {
        var credentials = Credentials()
        if (System.getenv("FGA_CLIENT_ID") != null) {
            credentials = Credentials(
                ClientCredentials()
                    .apiAudience(System.getenv("FGA_API_AUDIENCE"))
                    .apiTokenIssuer(System.getenv("FGA_TOKEN_ISSUER"))
                    .clientId("FGA_CLIENT_ID")
                    .clientSecret("FGA_CLIENT_SECRET")
            )
        } else {
            println("Proceeding with no credentials (expecting localhost)")
        }
        val configuration = ClientConfiguration()
            .apiUrl(System.getenv("FGA_API_URL")) // required, e.g. https://api.fga.example
            .storeId(System.getenv("FGA_STORE_ID")) // not needed when calling `CreateStore` or `ListStores`
            .authorizationModelId(
                System.getenv("FGA_AUTHORIZATION_MODEL_ID")
            ) // Optional, can be overridden per request
            .credentials(credentials)
        val fgaClient = OpenFgaClient(configuration)

        // ListStores
        println("Listing Stores")
        val stores1 = fgaClient.listStores().get()
        println("Stores Count: " + stores1.stores.size)

        // CreateStore
        println("Creating Test Store")
        val store = fgaClient
            .createStore(CreateStoreRequest().name("Test Store"))
            .get()
        println("Test Store ID: " + store.id)

        // Set the store id
        fgaClient.setStoreId(store.id)

        // ListStores after Create
        println("Listing Stores")
        val stores = fgaClient.listStores().get()
        println("Stores Count: " + stores.stores.size)

        // GetStore
        println("Getting Current Store")
        val currentStore = fgaClient.store.get()
        println("Current Store Name: " + currentStore.name)

        // ReadAuthorizationModels
        println("Reading Authorization Models")
        var models = fgaClient.readAuthorizationModels().get()
        println("Models Count: " + models.authorizationModels.size)

        // ReadLatestAuthorizationModel
        try {
            val latestAuthorizationModel = fgaClient
                .readLatestAuthorizationModel()
                .get() // TODO: Should this really return null? Optional<...Response>?
            println(
                "Latest Authorization Model ID "
                        + latestAuthorizationModel.authorizationModel!!.id
            )
        } catch (e: Exception) {
            println("Latest Authorization Model not found")
        }
        val mapper = ObjectMapper().findAndRegisterModules()

        // WriteAuthorizationModel
        val authModelJson = loadResource("example1-auth-model.json")
        val authorizationModel = fgaClient
            .writeAuthorizationModel(mapper.readValue(authModelJson, WriteAuthorizationModelRequest::class.java))
            .get()
        println("Authorization Model ID " + authorizationModel.authorizationModelId)

        // ReadAuthorizationModels - after Write
        println("Reading Authorization Models")
        models = fgaClient.readAuthorizationModels().get()
        println("Models Count: " + models.authorizationModels.size)

        // ReadLatestAuthorizationModel - after Write
        val latestAuthorizationModel = fgaClient.readLatestAuthorizationModel().get()
        println(
            "Latest Authorization Model ID "
                    + latestAuthorizationModel.authorizationModel!!.id
        )

        // Set the model ID
        fgaClient.setAuthorizationModelId(
            latestAuthorizationModel.authorizationModel!!.id
        )

        // Write
        println("Writing Tuples")
        fgaClient
            .write(
                ClientWriteRequest()
                    .writes(
                        listOf(
                            ClientTupleKey()
                                .user("user:anne")
                                .relation("writer")
                                ._object("document:roadmap")
                        )
                    ),
                ClientWriteOptions()
                    .disableTransactions(true)
                    .authorizationModelId(authorizationModel.authorizationModelId)
            )
            .get()
        println("Done Writing Tuples")

        // Read
        println("Reading Tuples")
        val readTuples = fgaClient.read(ClientReadRequest()).get()
        println("Read Tuples" + mapper.writeValueAsString(readTuples))

        // ReadChanges
        println("Reading Tuple Changess")
        val readChangesTuples = fgaClient.readChanges(ClientReadChangesRequest()).get()
        println("Read Changes Tuples" + mapper.writeValueAsString(readChangesTuples))

        // Check
        println("Checking for access")
        try {
            val failingCheckResponse = fgaClient
                .check(
                    ClientCheckRequest()
                        .user("user:anne")
                        .relation("reader")
                        ._object("document:roadmap")
                )
                .get()
            println("Allowed: " + failingCheckResponse.allowed)
        } catch (e: Exception) {
            println("Failed due to: " + e.message)
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
            .writeAssertions(
                listOf(
                    ClientAssertion()
                        .user("user:carl")
                        .relation("writer")
                        ._object("document:budget")
                        .expectation(true),
                    ClientAssertion()
                        .user("user:anne")
                        .relation("reader")
                        ._object("document:roadmap")
                        .expectation(false)
                )
            )
            .get()
        println("Assertions updated")

        // ReadAssertions
        println("Reading Assertions")
        val assertions = fgaClient.readAssertions().get()
        println("Assertions " + mapper.writeValueAsString(assertions))

        // DeleteStore
        println("Deleting Current Store")
        fgaClient.deleteStore().get()
        println("Deleted Store: " + currentStore.name)
    }

    // Small helper function to load resource files relative to this class.
    private fun loadResource(filename: String): String {
        return javaClass.module.classLoader.getResource(filename)?.readText()!!
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("=== Example 1 (Kotlin) ===")
            try {
                KotlinExample1().run()
            } catch (e: Exception) {
                println("ERROR: $e")
            }
        }
    }
}
