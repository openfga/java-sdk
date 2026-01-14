package dev.openfga.sdk.example;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.RawRequestBuilder;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import java.util.Map;

/**
 * Example demonstrating the Raw API "Escape Hatch" functionality.
 *
 * This example shows how to use the Raw API to call experimental or newly-released
 * OpenFGA endpoints that may not yet be supported in the typed SDK methods.
 */
public class RawApiExample {

    /**
     * Custom response type for demonstration.
     */
    public static class BulkDeleteResponse {
        public int deletedCount;
        public String message;
    }

    /**
     * Custom response type for demonstration.
     */
    public static class ExperimentalFeatureResponse {
        public boolean enabled;
        public String version;
        public Map<String, Object> metadata;
    }

    public static void main(String[] args) throws Exception {
        // Initialize the OpenFGA client
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl("http://localhost:8080")
                .storeId("01YCP46JKYM8FJCQ37NMBYHE5X");

        OpenFgaClient fgaClient = new OpenFgaClient(config);

        // Example 1: Call a POST endpoint with typed response
        System.out.println("Example 1: POST request with typed response");
        postRequestExample(fgaClient);

        // Example 2: GET request with raw JSON response
        System.out.println("\nExample 2: GET request with raw JSON");
        rawJsonExample(fgaClient);

        // Example 3: Request with query parameters
        System.out.println("\nExample 3: Request with query parameters");
        queryParametersExample(fgaClient);

        // Example 4: Request with custom headers
        System.out.println("\nExample 4: Request with custom headers");
        customHeadersExample(fgaClient);

        // Example 5: Error handling
        System.out.println("\nExample 5: Error handling");
        errorHandlingExample(fgaClient);
    }

    /**
     * Example 1: POST request with request body and typed response.
     */
    private static void postRequestExample(OpenFgaClient fgaClient) {
        try {
            // Build the raw request
            RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/bulk-delete")
                    .pathParam("store_id", "01YCP46JKYM8FJCQ37NMBYHE5X")
                    .queryParam("force", "true")
                    .body(Map.of(
                            "older_than", "2023-01-01",
                            "type", "user",
                            "limit", 1000));

            // Execute with typed response
            fgaClient
                    .raw()
                    .send(request, BulkDeleteResponse.class)
                    .thenAccept(response -> {
                        System.out.println("Status: " + response.getStatusCode());
                        System.out.println("Deleted items: " + response.getData().deletedCount);
                        System.out.println("Message: " + response.getData().message);
                    })
                    .exceptionally(e -> {
                        System.err.println("Error: " + e.getMessage());
                        return null;
                    })
                    .get(); // Wait for completion (in production, avoid blocking)

        } catch (Exception e) {
            System.err.println("Failed to execute bulk delete: " + e.getMessage());
        }
    }

    /**
     * Example 2: Get raw JSON response without deserialization.
     * This is useful when you want to inspect the response or don't have a Java class.
     */
    private static void rawJsonExample(OpenFgaClient fgaClient) {
        try {
            RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/experimental-feature")
                    .pathParam("store_id", "01YCP46JKYM8FJCQ37NMBYHE5X");

            // Execute and get raw JSON string
            fgaClient
                    .raw()
                    .send(request) // No class specified = returns String
                    .thenAccept(response -> {
                        System.out.println("Status: " + response.getStatusCode());
                        System.out.println("Raw JSON: " + response.getRawResponse());
                    })
                    .exceptionally(e -> {
                        System.err.println("Error: " + e.getMessage());
                        return null;
                    })
                    .get();

        } catch (Exception e) {
            System.err.println("Failed to get raw response: " + e.getMessage());
        }
    }

    /**
     * Example 3: Using query parameters for filtering or pagination.
     */
    private static void queryParametersExample(OpenFgaClient fgaClient) {
        try {
            RawRequestBuilder request =
                    RawRequestBuilder.builder("GET", "/stores/{store_id}/experimental-list")
                            .pathParam("store_id", "01YCP46JKYM8FJCQ37NMBYHE5X")
                            .queryParam("page", "1")
                            .queryParam("limit", "50")
                            .queryParam("filter", "active");

            fgaClient
                    .raw()
                    .send(request, ExperimentalFeatureResponse.class)
                    .thenAccept(response -> {
                        System.out.println("Status: " + response.getStatusCode());
                        System.out.println("Feature enabled: " + response.getData().enabled);
                        System.out.println("Version: " + response.getData().version);
                    })
                    .exceptionally(e -> {
                        System.err.println("Error: " + e.getMessage());
                        return null;
                    })
                    .get();

        } catch (Exception e) {
            System.err.println("Failed to call endpoint with query params: " + e.getMessage());
        }
    }

    /**
     * Example 4: Adding custom headers to requests.
     */
    private static void customHeadersExample(OpenFgaClient fgaClient) {
        try {
            RawRequestBuilder request = RawRequestBuilder.builder("POST", "/stores/{store_id}/experimental-action")
                    .pathParam("store_id", "01YCP46JKYM8FJCQ37NMBYHE5X")
                    .header("X-Request-ID", "unique-request-123")
                    .header("X-Client-Version", "1.0.0")
                    .body(Map.of("action", "test"));

            fgaClient
                    .raw()
                    .send(request, ExperimentalFeatureResponse.class)
                    .thenAccept(response -> {
                        System.out.println("Status: " + response.getStatusCode());
                        System.out.println("Response: " + response.getData());
                    })
                    .exceptionally(e -> {
                        System.err.println("Error: " + e.getMessage());
                        return null;
                    })
                    .get();

        } catch (Exception e) {
            System.err.println("Failed to call endpoint with custom headers: " + e.getMessage());
        }
    }

    /**
     * Example 5: Error handling with the Raw API.
     * The Raw API automatically benefits from the SDK's error handling and retries.
     */
    private static void errorHandlingExample(OpenFgaClient fgaClient) {
        try {
            RawRequestBuilder request = RawRequestBuilder.builder("GET", "/stores/{store_id}/non-existent")
                    .pathParam("store_id", "01YCP46JKYM8FJCQ37NMBYHE5X");

            fgaClient
                    .raw()
                    .send(request)
                    .thenAccept(response -> {
                        System.out.println("Success: " + response.getStatusCode());
                    })
                    .exceptionally(e -> {
                        // Standard SDK error handling works here:
                        // - 401: Unauthorized
                        // - 404: Not Found
                        // - 500: Internal Server Error (with automatic retries)
                        System.err.println("API Error: " + e.getMessage());
                        if (e.getCause() != null) {
                            System.err.println("Cause: " + e.getCause().getClass().getName());
                        }
                        return null;
                    })
                    .get();

        } catch (Exception e) {
            System.err.println("Failed with error: " + e.getMessage());
        }
    }
}

