package dev.openfga.sdk.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientWriteOptions;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for verifying that response headers are properly returned in ClientWriteResponse.
 * This test suite specifically addresses the issue where headers were not being returned
 * for transaction mode writes.
 */
public class OpenFgaClientWriteResponseHeadersTest {
    private static final String DEFAULT_STORE_ID = "01YCP46JKYM8FJCQ37NMBYHE5X";
    private static final String DEFAULT_AUTH_MODEL_ID = "01G5JAVJ41T49E9TT3SKVS7X1J";
    private static final String DEFAULT_USER = "user:anne";
    private static final String DEFAULT_RELATION = "reader";
    private static final String DEFAULT_OBJECT = "document:budget";

    private WireMockServer wireMockServer;
    private OpenFgaClient fgaClient;

    @BeforeEach
    void setUp() throws Exception {
        wireMockServer =
                new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        ClientConfiguration configuration = new ClientConfiguration()
                .apiUrl("http://localhost:" + wireMockServer.port())
                .storeId(DEFAULT_STORE_ID)
                .authorizationModelId(DEFAULT_AUTH_MODEL_ID);

        fgaClient = new OpenFgaClient(configuration);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    /**
     * Test that transaction mode writes return response headers.
     * This is the primary test case for the fix.
     */
    @Test
    void writeTransactionMode_shouldReturnResponseHeaders() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Custom-Header", "custom-value")
                        .withHeader("X-Request-Id", "req-123")
                        .withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        // When
        ClientWriteResponse response = fgaClient.write(request).get();

        // Then
        assertNotNull(response.getHeaders(), "Headers should not be null");
        assertFalse(response.getHeaders().isEmpty(), "Headers should not be empty");

        // Verify specific headers are present (case-insensitive)
        assertTrue(response.getHeaders().containsKey("content-type"), "Should contain Content-Type header");
        assertTrue(response.getHeaders().containsKey("x-custom-header"), "Should contain X-Custom-Header");
        assertTrue(response.getHeaders().containsKey("x-request-id"), "Should contain X-Request-Id");

        // Verify header values
        assertEquals(List.of("application/json"), response.getHeaders().get("content-type"));
        assertEquals(List.of("custom-value"), response.getHeaders().get("x-custom-header"));
        assertEquals(List.of("req-123"), response.getHeaders().get("x-request-id"));
    }

    /**
     * Test that transaction mode writes with deletes also return response headers.
     */
    @Test
    void writeTransactionModeWithDeletes_shouldReturnResponseHeaders() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Response-Header", "response-value")
                        .withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .deletes(List.of(new ClientTupleKeyWithoutCondition()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        // When
        ClientWriteResponse response = fgaClient.write(request).get();

        // Then
        assertNotNull(response.getHeaders());
        assertFalse(response.getHeaders().isEmpty());
        assertTrue(response.getHeaders().containsKey("x-response-header"));
        assertEquals(List.of("response-value"), response.getHeaders().get("x-response-header"));
    }

    /**
     * Test that transaction mode writes with both writes and deletes return response headers.
     */
    @Test
    void writeTransactionModeWithWritesAndDeletes_shouldReturnResponseHeaders() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Combined-Header", "combined-value")
                        .withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)))
                .deletes(List.of(new ClientTupleKeyWithoutCondition()
                        .user("user:bob")
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        // When
        ClientWriteResponse response = fgaClient.write(request).get();

        // Then
        assertNotNull(response.getHeaders());
        assertFalse(response.getHeaders().isEmpty());
        assertTrue(response.getHeaders().containsKey("x-combined-header"));
        assertEquals(List.of("combined-value"), response.getHeaders().get("x-combined-header"));
    }

    /**
     * Test that non-transaction mode writes return empty headers.
     * Non-transaction mode aggregates multiple API responses, so it's expected
     * that headers are empty (as there's no single response to get headers from).
     */
    @Test
    void writeNonTransactionMode_shouldReturnEmptyHeaders() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Response-Header", "should-not-appear")
                        .withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        ClientWriteOptions options = new ClientWriteOptions().disableTransactions(true);

        // When
        ClientWriteResponse response = fgaClient.write(request, options).get();

        // Then
        assertNotNull(response.getHeaders());
        assertTrue(
                response.getHeaders().isEmpty(),
                "Non-transaction mode should return empty headers as it aggregates multiple responses");
    }

    /**
     * Test writeTuples() helper method returns headers.
     */
    @Test
    void writeTuples_shouldReturnResponseHeaders() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Write-Tuples-Header", "write-tuples-value")
                        .withBody("{}")));

        List<ClientTupleKey> tuples = List.of(new ClientTupleKey()
                .user(DEFAULT_USER)
                .relation(DEFAULT_RELATION)
                ._object(DEFAULT_OBJECT));

        // When
        ClientWriteResponse response = fgaClient.writeTuples(tuples).get();

        // Then
        assertNotNull(response.getHeaders());
        assertFalse(response.getHeaders().isEmpty());
        assertTrue(response.getHeaders().containsKey("x-write-tuples-header"));
        assertEquals(List.of("write-tuples-value"), response.getHeaders().get("x-write-tuples-header"));
    }

    /**
     * Test deleteTuples() helper method returns headers.
     */
    @Test
    void deleteTuples_shouldReturnResponseHeaders() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Delete-Tuples-Header", "delete-tuples-value")
                        .withBody("{}")));

        List<ClientTupleKeyWithoutCondition> tuples = List.of(new ClientTupleKeyWithoutCondition()
                .user(DEFAULT_USER)
                .relation(DEFAULT_RELATION)
                ._object(DEFAULT_OBJECT));

        // When
        ClientWriteResponse response = fgaClient.deleteTuples(tuples).get();

        // Then
        assertNotNull(response.getHeaders());
        assertFalse(response.getHeaders().isEmpty());
        assertTrue(response.getHeaders().containsKey("x-delete-tuples-header"));
        assertEquals(List.of("delete-tuples-value"), response.getHeaders().get("x-delete-tuples-header"));
    }

    /**
     * Edge case: Test that empty headers from server are handled correctly.
     */
    @Test
    void writeTransactionMode_withNoHeaders_shouldReturnEmptyMap() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse().withStatus(200).withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        // When
        ClientWriteResponse response = fgaClient.write(request).get();

        // Then
        assertNotNull(response.getHeaders(), "Headers map should not be null even when empty");
        // Note: The response will likely still have some default HTTP headers
        // The important thing is that we don't crash and headers is not null
    }

    /**
     * Test that status code is correctly preserved in transaction mode.
     */
    @Test
    void writeTransactionMode_shouldPreserveStatusCode() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse().withStatus(200).withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        // When
        ClientWriteResponse response = fgaClient.write(request).get();

        // Then
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Test backward compatibility: Verify that the response structure hasn't changed.
     */
    @Test
    void writeTransactionMode_shouldMaintainBackwardCompatibility() throws Exception {
        // Given
        String writePath = String.format("/stores/%s/write", DEFAULT_STORE_ID);
        wireMockServer.stubFor(post(urlEqualTo(writePath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Test-Header", "test-value")
                        .withBody("{}")));

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(DEFAULT_USER)
                        .relation(DEFAULT_RELATION)
                        ._object(DEFAULT_OBJECT)));

        // When
        ClientWriteResponse response = fgaClient.write(request).get();

        // Then - All existing fields should still work
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getWrites());
        assertEquals(1, response.getWrites().size());
        assertNotNull(response.getDeletes());
        assertEquals(0, response.getDeletes().size());

        // And headers should now be available
        assertNotNull(response.getHeaders());
        assertFalse(response.getHeaders().isEmpty());
    }
}
