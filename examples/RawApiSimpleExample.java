package dev.openfga.sdk.examples;

import dev.openfga.sdk.api.client.*;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple example demonstrating Raw API usage.
 *
 * Run this with your local OpenFGA instance:
 * docker run --rm -p 8080:8080 -p 3000:3000 openfga/openfga run
 *
 * Then compile and run this example.
 */
public class RawApiSimpleExample {

    public static void main(String[] args) {
        try {
            // Initialize client
            ClientConfiguration config = new ClientConfiguration()
                .apiUrl("http://localhost:8080");

            OpenFgaClient client = new OpenFgaClient(config);

            System.out.println("=== Raw API Simple Example ===\n");

            // Example 1: List stores (raw JSON)
            System.out.println("1. Listing stores (raw JSON):");
            RawRequestBuilder listRequest = RawRequestBuilder.builder("GET", "/stores");
            ApiResponse<String> listResponse = client.raw().send(listRequest).get();
            System.out.println("   Status: " + listResponse.getStatusCode());
            System.out.println("   Raw JSON: " + listResponse.getData());
            System.out.println();

            // Example 2: Create a store (typed response)
            System.out.println("2. Creating a store (typed response):");
            Map<String, Object> createBody = new HashMap<>();
            createBody.put("name", "raw-api-demo-store");

            RawRequestBuilder createRequest = RawRequestBuilder.builder("POST", "/stores")
                .body(createBody);

            ApiResponse<CreateStoreResponse> createResponse =
                client.raw().send(createRequest, CreateStoreResponse.class).get();

            String storeId = createResponse.getData().getId();
            String storeName = createResponse.getData().getName();

            System.out.println("   Status: " + createResponse.getStatusCode());
            System.out.println("   Store ID: " + storeId);
            System.out.println("   Store Name: " + storeName);
            System.out.println();

            // Example 3: Get store with path parameter
            System.out.println("3. Getting store by ID:");
            RawRequestBuilder getRequest = RawRequestBuilder.builder("GET", "/stores/{store_id}")
                .pathParam("store_id", storeId);

            ApiResponse<GetStoreResponse> getResponse =
                client.raw().send(getRequest, GetStoreResponse.class).get();

            System.out.println("   Status: " + getResponse.getStatusCode());
            System.out.println("   Retrieved store: " + getResponse.getData().getName());
            System.out.println();

            // Example 4: Automatic store_id replacement
            System.out.println("4. Using automatic {store_id} replacement:");
            client.setStoreId(storeId);

            // No need to call .pathParam("store_id", ...)
            RawRequestBuilder autoRequest = RawRequestBuilder.builder("GET", "/stores/{store_id}");
            ApiResponse<GetStoreResponse> autoResponse =
                client.raw().send(autoRequest, GetStoreResponse.class).get();

            System.out.println("   Status: " + autoResponse.getStatusCode());
            System.out.println("   Store auto-fetched: " + autoResponse.getData().getName());
            System.out.println();

            // Example 5: Query parameters
            System.out.println("5. List stores with pagination:");
            RawRequestBuilder paginatedRequest = RawRequestBuilder.builder("GET", "/stores")
                .queryParam("page_size", "5");

            ApiResponse<ListStoresResponse> paginatedResponse =
                client.raw().send(paginatedRequest, ListStoresResponse.class).get();

            System.out.println("   Status: " + paginatedResponse.getStatusCode());
            System.out.println("   Stores returned: " + paginatedResponse.getData().getStores().size());
            System.out.println();

            // Example 6: Custom headers
            System.out.println("6. Request with custom headers:");
            RawRequestBuilder headerRequest = RawRequestBuilder.builder("GET", "/stores/{store_id}")
                .pathParam("store_id", storeId)
                .header("X-Custom-Header", "my-value")
                .header("X-Request-ID", "demo-123");

            ApiResponse<GetStoreResponse> headerResponse =
                client.raw().send(headerRequest, GetStoreResponse.class).get();

            System.out.println("   Status: " + headerResponse.getStatusCode());
            System.out.println("   Custom headers sent successfully");
            System.out.println();

            System.out.println("=== All examples completed successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

