package dev.openfga.sdk.api.client;

/**
 * Simple compilation test to verify HttpMethod enum and ApiExecutorRequestBuilder work together.
 */
public class HttpMethodCompilationTest {
    public static void main(String[] args) {
        // Test 1: Using HttpMethod enum (new way)
        ApiExecutorRequestBuilder request1 = ApiExecutorRequestBuilder.builder(HttpMethod.GET, "/stores")
                .queryParam("page_size", "10")
                .build();

        System.out.println("✓ HttpMethod.GET works: " + request1.getMethod());

        // Test 2: Using HttpMethod enum with POST
        ApiExecutorRequestBuilder request2 = ApiExecutorRequestBuilder.builder(HttpMethod.POST, "/stores")
                .body("test")
                .build();

        System.out.println("✓ HttpMethod.POST works: " + request2.getMethod());

        // Test 3: All HTTP methods
        for (HttpMethod method : HttpMethod.values()) {
            ApiExecutorRequestBuilder request =
                    ApiExecutorRequestBuilder.builder(method, "/test").build();
            System.out.println("✓ " + method + " works: " + request.getMethod());
        }

        // Test 4: Verify getMethod() returns string
        ApiExecutorRequestBuilder request4 =
                ApiExecutorRequestBuilder.builder(HttpMethod.PUT, "/test").build();
        String methodString = request4.getMethod();
        assert "PUT".equals(methodString) : "Expected 'PUT' but got '" + methodString + "'";
        System.out.println("✓ getMethod() returns correct string: " + methodString);

        System.out.println("\n✓✓✓ All compilation tests passed! ✓✓✓");
    }
}
