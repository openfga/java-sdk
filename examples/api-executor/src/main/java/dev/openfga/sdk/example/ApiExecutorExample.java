package dev.openfga.sdk.example;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.ApiExecutorRequestBuilder;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.CreateStoreResponse;
import dev.openfga.sdk.api.model.ListStoresResponse;
import dev.openfga.sdk.api.model.Store;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Example demonstrating API Executor usage.
 *
 * This example shows how to use the API Executor to call OpenFGA endpoints
 * that are not yet wrapped by the SDK's typed methods.
 *
 * The example uses real OpenFGA endpoints to demonstrate actual functionality.
 *
 * Note: Examples use .get() to block for simplicity. In production, use async patterns:
 * - thenApply/thenAccept for chaining
 * - thenCompose for sequential async operations
 * - CompletableFuture.allOf for parallel operations
 */
public class ApiExecutorExample {

    public static void main(String[] args) throws Exception {
        // Initialize the OpenFGA client (no store ID needed for list stores)
        ClientConfiguration config = new ClientConfiguration().apiUrl("http://localhost:8080");

        OpenFgaClient fgaClient = new OpenFgaClient(config);

        System.out.println("=== API Executor Examples ===\n");

        // Example 1: List stores with typed response
        System.out.println("Example 1: List stores (GET with typed response)");
        String storeId = listStoresExample(fgaClient);

        // Example 2: Get store with raw JSON response
        System.out.println("\nExample 2: Get store (GET with raw JSON)");
        getStoreRawJsonExample(fgaClient, storeId);

        // Example 3: List stores with query parameters
        System.out.println("\nExample 3: List stores with pagination (query parameters)");
        listStoresWithPaginationExample(fgaClient);

        // Example 4: Create store with custom headers
        System.out.println("\nExample 4: Create store (POST with custom headers)");
        createStoreWithHeadersExample(fgaClient);

        // Example 5: Error handling - try to get non-existent store
        System.out.println("\nExample 5: Error handling (404 error)");
        errorHandlingExample(fgaClient);

        System.out.println("\n=== All examples completed ===");
    }

    /**
     * Example 1: GET request with typed response.
     * Lists all stores using the API Executor and returns a store ID for use in other examples.
     */
    private static String listStoresExample(OpenFgaClient fgaClient) {
        try {
            // Build the raw request for GET /stores
            ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores").build();

            // Execute with typed response
            var response = fgaClient
                    .apiExecutor()
                    .send(request, ListStoresResponse.class)
                    .get();

            System.out.println("✓ Status: " + response.getStatusCode());
            List<Store> stores = response.getData().getStores();
            System.out.println("✓ Found " + stores.size() + " store(s)");

            if (!stores.isEmpty()) {
                Store firstStore = stores.get(0);
                System.out.println("✓ First store: " + firstStore.getName() + " (ID: " + firstStore.getId() + ")");
                return firstStore.getId();
            } else {
                // Create a store if none exist
                System.out.println("  No stores found, creating one...");
                return createStoreForExamples(fgaClient);
            }

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            // Create a store on error
            try {
                return createStoreForExamples(fgaClient);
            } catch (Exception ex) {
                return "01YCP46JKYM8FJCQ37NMBYHE5X"; // fallback
            }
        }
    }

    /**
     * Helper method to create a store for examples.
     */
    private static String createStoreForExamples(OpenFgaClient fgaClient) throws Exception {
        String storeName = "api-executor-example-" + UUID.randomUUID().toString().substring(0, 8);
        ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores")
                .body(Map.of("name", storeName))
                .build();

        // Use typed response instead of manual JSON parsing
        var response = fgaClient.apiExecutor().send(request, CreateStoreResponse.class).get();
        System.out.println("  Created store: " + storeName);
        return response.getData().getId();
    }

    /**
     * Example 2: Get raw JSON response without deserialization.
     */
    private static void getStoreRawJsonExample(OpenFgaClient fgaClient, String storeId) {
        try {
            ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}")
                    .pathParam("store_id", storeId)
                    .build();

            // Execute and get raw JSON string
            var response = fgaClient.apiExecutor().send(request).get();

            System.out.println("✓ Status: " + response.getStatusCode());
            System.out.println("✓ Raw JSON: " + response.getData());
            System.out.println("✓ Content-Type: " + response.getHeaders().get("content-type"));

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Example 3: Add query parameters to requests.
     */
    private static void listStoresWithPaginationExample(OpenFgaClient fgaClient) {
        try {
            ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores")
                    .queryParam("page_size", "2")
                    .build();

            var response = fgaClient
                    .apiExecutor()
                    .send(request, ListStoresResponse.class)
                    .get();

            System.out.println("✓ Status: " + response.getStatusCode());
            System.out.println("✓ Stores returned: " + response.getData().getStores().size());
            if (response.getData().getContinuationToken() != null) {
                String token = response.getData().getContinuationToken();
                String tokenPreview = token.length() > 20 ? token.substring(0, 20) + "..." : token;
                System.out.println("✓ Continuation token present: " + tokenPreview);
            } else {
                System.out.println("✓ No continuation token (all results returned)");
            }

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Example 4: Add custom headers to requests.
     */
    private static void createStoreWithHeadersExample(OpenFgaClient fgaClient) {
        try {
            String storeName = "raw-api-custom-headers-" + UUID.randomUUID().toString().substring(0, 8);
            ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("POST", "/stores")
                    .header("X-Example-Header", "custom-value")
                    .header("X-Request-ID", "req-" + UUID.randomUUID())
                    .body(Map.of("name", storeName))
                    .build();

            var response = fgaClient.apiExecutor().send(request).get();

            System.out.println("✓ Status: " + response.getStatusCode());
            System.out.println("✓ Store created successfully");
            String responseData = response.getData();
            String preview = responseData.length() > 100 ? responseData.substring(0, 100) + "..." : responseData;
            System.out.println("✓ Response: " + preview);

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Example 5: Error handling with the API Executor.
     * Requests use the SDK's error handling and retry logic.
     */
    private static void errorHandlingExample(OpenFgaClient fgaClient) {
        try {
            // Try to get a non-existent store
            ApiExecutorRequestBuilder request = ApiExecutorRequestBuilder.builder("GET", "/stores/{store_id}")
                    .pathParam("store_id", "01ZZZZZZZZZZZZZZZZZZZZZZZ9")
                    .build();

            var response = fgaClient.apiExecutor().send(request).get();

            System.out.println("✓ Success: " + response.getStatusCode());

        } catch (Exception e) {
            // Expected error - demonstrates proper error handling
            System.out.println("✓ Error handled correctly:");
            System.out.println("  Message: " + e.getMessage());
            System.out.println("  Type: " + e.getCause().getClass().getSimpleName());
        }
    }
}

