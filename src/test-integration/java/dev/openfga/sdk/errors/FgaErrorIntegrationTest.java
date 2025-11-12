package dev.openfga.sdk.errors;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.openfga.OpenFGAContainer;

@TestInstance(Lifecycle.PER_CLASS)
@Testcontainers
class FgaErrorIntegrationTest {

    @Container
    private static final OpenFGAContainer openfga = new OpenFGAContainer("openfga/openfga:v1.10.2");

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private static WriteAuthorizationModelRequest authModelRequest;

    private OpenFgaClient fga;
    private String storeId;

    @BeforeAll
    static void loadAuthModel() throws IOException {
        String authModelJson = Files.readString(Paths.get("src", "test-integration", "resources", "auth-model.json"));
        authModelRequest = mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class);
    }

    @BeforeEach
    void setUp() throws Exception {
        ClientConfiguration config = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
        fga = new OpenFgaClient(config);

        CreateStoreResponse storeResponse =
                fga.createStore(new CreateStoreRequest().name("test-store")).get();
        storeId = storeResponse.getId();
        fga.setStoreId(storeId);
    }

    @Test
    void testValidationError_InvalidType() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientCheckRequest request =
                new ClientCheckRequest().user("user:123").relation("viewer")._object("invalid_type:doc1");

        CompletableFuture<ClientCheckResponse> future = fga.check(request);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        String exceptionMessage = exception.getMessage();
        assertNotNull(exceptionMessage);
        assertTrue(exceptionMessage.contains("FgaApiValidationError"), "Should include error class name");
        assertTrue(exceptionMessage.contains("[check]"), "Should include operation name");
        assertTrue(
                exceptionMessage.contains("type 'invalid_type' not found"), "Should include actual error from server");

        Throwable cause = exception.getCause();
        assertInstanceOf(FgaApiValidationError.class, cause);

        FgaApiValidationError error = (FgaApiValidationError) cause;
        assertEquals("check", error.getOperationName());
        assertEquals(400, error.getStatusCode());
        assertEquals("validation_error", error.getApiErrorCode());
        assertEquals("type 'invalid_type' not found", error.getApiErrorMessage());

        // Verify formatted messages
        String errorMessage = error.getMessage();
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("[check]"), "Should include operation name");
        assertTrue(errorMessage.contains("type 'invalid_type' not found"), "Should include server error");
        assertTrue(errorMessage.contains("validation_error"), "Should include error code");
    }

    @Test
    void testValidationError_InvalidRelation() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientCheckRequest request = new ClientCheckRequest()
                .user("user:123")
                .relation("invalid_relation")
                ._object("document:doc1");

        CompletableFuture<ClientCheckResponse> future = fga.check(request);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        // Verify ExecutionException message contains the full error details from server
        // Note: These assertions will fail if OpenFGA server changes its error message format,
        // which is intentional - integration tests should catch server behavior changes
        String exceptionMessage = exception.getMessage();
        assertNotNull(exceptionMessage);
        assertTrue(exceptionMessage.contains("FgaApiValidationError"), "Should include error class name");
        assertTrue(exceptionMessage.contains("[check]"), "Should include operation name");
        assertTrue(
                exceptionMessage.contains("relation 'document#invalid_relation' not found"),
                "Should include actual error from server");

        Throwable cause = exception.getCause();
        assertInstanceOf(FgaApiValidationError.class, cause);

        FgaApiValidationError error = (FgaApiValidationError) cause;

        // Verify error object contains expected details
        assertEquals("check", error.getOperationName());
        assertEquals(400, error.getStatusCode());
        assertEquals("validation_error", error.getApiErrorCode());
        assertEquals("relation 'document#invalid_relation' not found", error.getApiErrorMessage());

        // Verify formatted messages
        String errorMessage = error.getMessage();
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("[check]"), "Should include operation name");
        assertTrue(
                errorMessage.contains("relation 'document#invalid_relation' not found"), "Should include server error");
        assertTrue(errorMessage.contains("validation_error"), "Should include error code");
    }

    @Test
    void testValidationError_EmptyUser() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientCheckRequest request =
                new ClientCheckRequest().user("").relation("reader")._object("document:doc1");

        CompletableFuture<ClientCheckResponse> future = fga.check(request);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        Throwable cause = exception.getCause();
        assertInstanceOf(FgaApiValidationError.class, cause);

        FgaApiValidationError error = (FgaApiValidationError) cause;
        assertEquals(400, error.getStatusCode());
        assertNotNull(error.getApiErrorMessage());
    }

    @Test
    void testValidationError_InvalidStoreId() throws Exception {
        fga.setStoreId("01HZZZZZZZZZZZZZZZZZZZZZZ");

        CompletableFuture<ClientGetStoreResponse> future = fga.getStore();
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        Throwable cause = exception.getCause();
        assertInstanceOf(FgaApiValidationError.class, cause);

        FgaApiValidationError error = (FgaApiValidationError) cause;
        assertEquals(400, error.getStatusCode());
        assertNotNull(error.getMessage());
    }

    @Test
    void testValidationError_InvalidAuthorizationModelId() throws Exception {
        fga.setAuthorizationModelId("01HZZZZZZZZZZZZZZZZZZZZZZ");

        CompletableFuture<ClientReadAuthorizationModelResponse> future = fga.readAuthorizationModel();
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        Throwable cause = exception.getCause();
        assertInstanceOf(FgaApiValidationError.class, cause);

        FgaApiValidationError error = (FgaApiValidationError) cause;
        assertEquals(400, error.getStatusCode());
        assertEquals("readAuthorizationModel", error.getOperationName());
    }

    @Test
    void testErrorMetadataExtensibility() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientCheckRequest request =
                new ClientCheckRequest().user("user:123").relation("viewer")._object("invalid_type:doc1");

        CompletableFuture<ClientCheckResponse> future = fga.check(request);

        try {
            future.get();
            fail("Expected ExecutionException");
        } catch (ExecutionException e) {
            FgaError error = (FgaError) e.getCause();

            error.addMetadata("retry_attempt", 1);
            error.addMetadata("user_context", "admin");
            error.addMetadata("store_id", storeId);

            Map<String, Object> metadata = error.getMetadata();
            assertEquals(1, metadata.get("retry_attempt"));
            assertEquals("admin", metadata.get("user_context"));
            assertEquals(storeId, metadata.get("store_id"));
        }
    }

    @Test
    void testErrorMessageVisibilityInExecutionException() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientCheckRequest request =
                new ClientCheckRequest().user("user:123").relation("viewer")._object("invalid_type:doc1");

        CompletableFuture<ClientCheckResponse> future = fga.check(request);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        String exceptionMessage = exception.getMessage();
        assertNotNull(exceptionMessage);
        assertFalse(exceptionMessage.trim().isEmpty());

        FgaApiValidationError error = (FgaApiValidationError) exception.getCause();
        assertEquals("check", error.getOperationName());
        assertNotNull(error.getMessage());
        assertNotNull(error.getDetailedMessage());
        assertNotNull(error.toString());
    }

    @Test
    void testCompleteErrorContext() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientCheckRequest request =
                new ClientCheckRequest().user("user:123").relation("viewer")._object("invalid_type:doc1");

        CompletableFuture<ClientCheckResponse> future = fga.check(request);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        FgaApiValidationError error = (FgaApiValidationError) exception.getCause();

        assertEquals(400, error.getStatusCode());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getRequestUrl());
        assertEquals("check", error.getOperationName());
        assertNotNull(error.getApiErrorMessage());

        assertDoesNotThrow(() -> error.getRequestId());
        assertDoesNotThrow(() -> error.getApiErrorCode());

        assertNotNull(error.getMessage());
        assertFalse(error.getMessage().isEmpty());
        assertNotNull(error.getDetailedMessage());
        assertFalse(error.getDetailedMessage().isEmpty());
        assertNotNull(error.toString());
        assertTrue(error.toString().startsWith("FgaApiValidationError"));
    }

    @Test
    void testWriteValidationError_InvalidTupleKey() throws Exception {
        WriteAuthorizationModelResponse authModelResponse =
                fga.writeAuthorizationModel(authModelRequest).get();
        fga.setAuthorizationModelId(authModelResponse.getAuthorizationModelId());

        ClientWriteRequest writeRequest = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey().user("user:123").relation("reader")._object("invalid_type:doc1")));

        CompletableFuture<ClientWriteResponse> future = fga.write(writeRequest);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);

        Throwable cause = exception.getCause();
        assertInstanceOf(FgaApiValidationError.class, cause);

        FgaApiValidationError error = (FgaApiValidationError) cause;
        assertEquals("write", error.getOperationName());
        assertEquals(400, error.getStatusCode());
        assertNotNull(error.getApiErrorMessage());
    }
}
