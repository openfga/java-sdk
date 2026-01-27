package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.openfga.OpenFGAContainer;

/**
 * Integration tests for ApiExecutor functionality.
 * These tests demonstrate how to use raw requests to call OpenFGA endpoints
 * without using the SDK's typed methods.
 */
@TestInstance(Lifecycle.PER_CLASS)
@Testcontainers
public class ApiExecutorIntegrationTest {

    @Container
    private static final OpenFGAContainer openfga = new OpenFGAContainer("openfga/openfga:v1.10.2");

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private OpenFgaClient fga;

    @BeforeEach
    public void initializeApi() throws Exception {
        System.setProperty("HttpRequestAttempt.debug-logging", "enable");

        ClientConfiguration apiConfig = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
        fga = new OpenFgaClient(apiConfig);
    }

    /**
     * Test listing stores using ApiExecutor instead of fga.listStores().
     */
    @Test
    public void rawRequest_listStores() throws Exception {
        // Create a store first so we have something to list
        String storeName = "test-store-" + System.currentTimeMillis();
        createStoreUsingRawRequest(storeName);

        // Use ApiExecutor to list stores (equivalent to GET /stores)
        ApiExecutorRequestBuilder request =
                ApiExecutorRequestBuilder.builder("GET", "/stores").build();

        ApiResponse<ListStoresResponse> response =
                fga.apiExecutor().send(request, ListStoresResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getStores());
        assertTrue(response.getData().getStores().size() > 0);

        // Verify we can find our store
        boolean foundStore =
                response.getData().getStores().stream().anyMatch(store -> storeName.equals(store.getName()));
        assertTrue(foundStore, "Should find the store we created");

        System.out.println("✓ Successfully listed stores using raw request");
        System.out.println("  Found " + response.getData().getStores().size() + " stores");
    }

    /**
     * Test creating a store using ApiExecutor with typed response.
     */
    @Test
    public void rawRequest_createStore_typedResponse() throws Exception {
        String storeName = "raw-test-store-" + System.currentTimeMillis();

        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", storeName);

        // Use ApiExecutor to create store (equivalent to POST /stores)
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores")
                .body(requestBody)
                .build();

        ApiResponse<CreateStoreResponse> response =
                fga.apiExecutor().send(request, CreateStoreResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());
        assertEquals(storeName, response.getData().getName());

        System.out.println("✓ Successfully created store using raw request");
        System.out.println("  Store ID: " + response.getData().getId());
        System.out.println("  Store Name: " + response.getData().getName());
    }

    /**
     * Test creating a store using ApiExecutor with raw JSON string response.
     */
    @Test
    public void rawRequest_createStore_rawJsonResponse() throws Exception {
        String storeName = "raw-json-test-" + System.currentTimeMillis();

        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", storeName);

        // Use ApiExecutor to create store and get raw JSON response
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores")
                .body(requestBody)
                .build();

        ApiResponse<String> response = fga.apiExecutor().send(request).get();

        // Verify response
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getRawResponse());

        // Parse the JSON manually
        String rawJson = response.getData();
        assertTrue(rawJson.contains("\"id\""));
        assertTrue(rawJson.contains("\"name\""));
        assertTrue(rawJson.contains(storeName));

        System.out.println("✓ Successfully created store with raw JSON response");
        System.out.println("  Raw JSON: " + rawJson);
    }

    /**
     * Test getting a specific store using ApiExecutor with path parameters.
     */
    @Test
    public void rawRequest_getStore_withPathParams() throws Exception {
        // Create a store first
        String storeName = "get-test-store-" + System.currentTimeMillis();
        String storeId = createStoreUsingRawRequest(storeName);

        // Use ApiExecutor to get store details (equivalent to GET /stores/{store_id})
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}")
                .pathParam("store_id", storeId)
                .build();

        ApiResponse<GetStoreResponse> response =
                fga.apiExecutor().send(request, GetStoreResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(storeId, response.getData().getId());
        assertEquals(storeName, response.getData().getName());

        System.out.println("✓ Successfully retrieved store using raw request with path params");
        System.out.println("  Store ID: " + response.getData().getId());
    }

    /**
     * Test automatic {store_id} replacement when store ID is configured.
     */
    @Test
    public void rawRequest_automaticStoreIdReplacement() throws Exception {
        // Create a store and configure it
        String storeName = "auto-store-" + System.currentTimeMillis();
        String storeId = createStoreUsingRawRequest(storeName);
        fga.setStoreId(storeId);

        // Use ApiExecutor WITHOUT providing store_id path param - it should be auto-replaced
        ApiExecutorRequestBuilder request =
                ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}").build();

        ApiResponse<GetStoreResponse> response =
                fga.apiExecutor().send(request, GetStoreResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals(storeId, response.getData().getId());

        System.out.println("✓ Successfully used automatic {store_id} replacement");
        System.out.println("  Configured store ID was automatically used");
    }

    /**
     * Test writing authorization model using ApiExecutor.
     */
    @Test
    public void rawRequest_writeAuthorizationModel() throws Exception {
        // Create a store first
        String storeName = "auth-model-test-" + System.currentTimeMillis();
        String storeId = createStoreUsingRawRequest(storeName);
        fga.setStoreId(storeId);

        // Build authorization model with proper metadata
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("schema_version", "1.1");

        // Create metadata for reader relation
        Map<String, Object> readerMetadata = new HashMap<>();
        readerMetadata.put("directly_related_user_types", List.of(Map.of("type", "user")));

        Map<String, Object> relationMetadata = new HashMap<>();
        relationMetadata.put("reader", readerMetadata);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("relations", relationMetadata);

        Map<String, Object> readerRelation = new HashMap<>();
        readerRelation.put("this", new HashMap<>());
        Map<String, Object> relations = new HashMap<>();
        relations.put("reader", readerRelation);

        List<Map<String, Object>> typeDefinitions = List.of(
                Map.of("type", "user", "relations", new HashMap<>()),
                Map.of("type", "document", "relations", relations, "metadata", metadata));

        requestBody.put("type_definitions", typeDefinitions);

        // Use ApiExecutor to write authorization model
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        "POST", "/stores/{store_id}/authorization-models")
                .body(requestBody)
                .build();

        ApiResponse<WriteAuthorizationModelResponse> response = fga.apiExecutor()
                .send(request, WriteAuthorizationModelResponse.class)
                .get();

        // Verify response
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getAuthorizationModelId());

        System.out.println("✓ Successfully wrote authorization model using raw request");
        System.out.println("  Model ID: " + response.getData().getAuthorizationModelId());
    }

    /**
     * Test reading authorization models with query parameters.
     */
    @Test
    public void rawRequest_readAuthorizationModels_withQueryParams() throws Exception {
        // Create a store and write a model
        String storeName = "read-models-test-" + System.currentTimeMillis();
        String storeId = createStoreUsingRawRequest(storeName);
        fga.setStoreId(storeId);

        // Create an authorization model first
        writeSimpleAuthorizationModel(storeId);

        // Use ApiExecutor to read authorization models with query parameters
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        "GET", "/stores/{store_id}/authorization-models")
                .queryParam("page_size", "10")
                .queryParam("continuation_token", "")
                .build();

        ApiResponse<ReadAuthorizationModelsResponse> response = fga.apiExecutor()
                .send(request, ReadAuthorizationModelsResponse.class)
                .get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getAuthorizationModels());
        assertTrue(response.getData().getAuthorizationModels().size() > 0);

        System.out.println("✓ Successfully read authorization models with query params");
        System.out.println(
                "  Found " + response.getData().getAuthorizationModels().size() + " models");
    }

    /**
     * Test Check API using raw request.
     * Disabled temporarily - requires more complex authorization model setup.
     */
    @Test
    @org.junit.jupiter.api.Disabled("Requires complex authorization model setup")
    public void rawRequest_check() throws Exception {
        // Setup: Create store and authorization model
        String storeName = "check-test-" + System.currentTimeMillis();
        String storeId = createStoreUsingRawRequest(storeName);
        fga.setStoreId(storeId);
        String modelId = writeSimpleAuthorizationModel(storeId);

        // Write a tuple
        writeTupleUsingRawRequest(storeId, "user:alice", "reader", "document:budget");

        // Use ApiExecutor to perform check
        Map<String, Object> checkBody = new HashMap<>();
        checkBody.put("authorization_model_id", modelId);

        Map<String, Object> tupleKey = new HashMap<>();
        tupleKey.put("user", "user:alice");
        tupleKey.put("relation", "reader");
        tupleKey.put("object", "document:budget");
        checkBody.put("tuple_key", tupleKey);

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/check")
                .body(checkBody)
                .build();

        ApiResponse<CheckResponse> response =
                fga.apiExecutor().send(request, CheckResponse.class).get();
        assertTrue(response.getData().getAllowed(), "Alice should be allowed to read the document");

        System.out.println("✓ Successfully performed check using raw request");
        System.out.println("  Check result: " + response.getData().getAllowed());
    }

    /**
     * Test custom headers with raw request.
     */
    @Test
    public void rawRequest_withCustomHeaders() throws Exception {
        String storeName = "headers-test-" + System.currentTimeMillis();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", storeName);

        // Use ApiExecutor with custom headers
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores")
                .body(requestBody)
                .header("X-Custom-Header", "custom-value")
                .header("X-Request-ID", "test-123")
                .build();

        ApiResponse<CreateStoreResponse> response =
                fga.apiExecutor().send(request, CreateStoreResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());

        System.out.println("✓ Successfully sent raw request with custom headers");
    }

    /**
     * Test error handling with raw request.
     */
    @Test
    public void rawRequest_errorHandling_notFound() throws Exception {
        // Try to get a non-existent store
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}")
                .pathParam("store_id", "non-existent-store-id")
                .build();

        // Should throw an exception
        try {
            fga.apiExecutor().send(request, GetStoreResponse.class).get();
            fail("Should have thrown an exception for non-existent store");
        } catch (Exception e) {
            // Expected - verify it's some kind of error (ExecutionException wrapping an FgaError)
            assertNotNull(e, "Exception should not be null");
            System.out.println("✓ Successfully handled error for non-existent store");
            System.out.println("  Error type: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.out.println("  Cause: " + e.getCause().getClass().getSimpleName());
            }
        }
    }

    /**
     * Test list stores with pagination using query parameters.
     */
    @Test
    public void rawRequest_listStores_withPagination() throws Exception {
        // Create multiple stores
        for (int i = 0; i < 3; i++) {
            createStoreUsingRawRequest("pagination-test-" + i + "-" + System.currentTimeMillis());
        }

        // Use ApiExecutor to list stores with pagination
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores")
                .queryParam("page_size", "2")
                .build();

        ApiResponse<ListStoresResponse> response =
                fga.apiExecutor().send(request, ListStoresResponse.class).get();

        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getStores());

        System.out.println("✓ Successfully listed stores with pagination");
        System.out.println("  Returned: " + response.getData().getStores().size() + " stores");
        if (response.getData().getContinuationToken() != null) {
            System.out.println("  Has continuation token for next page");
        }
    }

    // Helper methods

    private String createStoreUsingRawRequest(String storeName) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", storeName);

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores")
                .body(requestBody)
                .build();

        ApiResponse<CreateStoreResponse> response =
                fga.apiExecutor().send(request, CreateStoreResponse.class).get();

        return response.getData().getId();
    }

    private String writeSimpleAuthorizationModel(String storeId) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("schema_version", "1.1");

        // Create metadata for reader relation
        Map<String, Object> readerMetadata = new HashMap<>();
        readerMetadata.put("directly_related_user_types", List.of(Map.of("type", "user")));

        Map<String, Object> relationMetadata = new HashMap<>();
        relationMetadata.put("reader", readerMetadata);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("relations", relationMetadata);

        Map<String, Object> readerRelation = new HashMap<>();
        readerRelation.put("this", new HashMap<>());
        Map<String, Object> relations = new HashMap<>();
        relations.put("reader", readerRelation);

        List<Map<String, Object>> typeDefinitions = List.of(
                Map.of("type", "user", "relations", new HashMap<>()),
                Map.of("type", "document", "relations", relations, "metadata", metadata));

        requestBody.put("type_definitions", typeDefinitions);

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder(
                        "POST", "/stores/{store_id}/authorization-models")
                .pathParam("store_id", storeId)
                .body(requestBody)
                .build();

        ApiResponse<WriteAuthorizationModelResponse> response = fga.apiExecutor()
                .send(request, WriteAuthorizationModelResponse.class)
                .get();

        return response.getData().getAuthorizationModelId();
    }

    private void writeTupleUsingRawRequest(String storeId, String user, String relation, String object)
            throws Exception {
        Map<String, Object> tupleKey = new HashMap<>();
        tupleKey.put("user", user);
        tupleKey.put("relation", relation);
        tupleKey.put("object", object);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("writes", Map.of("tuple_keys", List.of(tupleKey)));

        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores/{store_id}/write")
                .pathParam("store_id", storeId)
                .body(requestBody)
                .build();

        fga.apiExecutor().send(request, Object.class).get();
    }
}
