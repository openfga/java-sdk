package dev.openfga.sdk.errors;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
public class FgaErrorIntegrationTest {

    @Container
    private static final OpenFGAContainer openfga = new OpenFGAContainer("openfga/openfga:v1.10.2");

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private String authModelJson;
    private OpenFgaClient fga;

    @BeforeAll
    public void loadAuthModelJson() throws IOException {
        authModelJson = Files.readString(Paths.get("src", "test-integration", "resources", "auth-model.json"));
    }

    @BeforeEach
    public void initializeApi() throws Exception {
        ClientConfiguration apiConfig = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
        fga = new OpenFgaClient(apiConfig);

        // Create a store
        String storeName = "ErrorTestStore";
        var createStoreResponse =
                fga.createStore(new CreateStoreRequest().name(storeName)).get();
        String storeId = createStoreResponse.getId();
        fga.setStoreId(storeId);

        // Write the authorization model
        WriteAuthorizationModelRequest writeModelRequest =
                mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class);
        fga.writeAuthorizationModel(writeModelRequest).get();
    }

    @Test
    public void testWriteValidationError() throws Exception {
        // Try to write a tuple with invalid type
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());
        assertTrue(exception.getMessage().contains("type 'invalid_type' not found"));
        assertEquals("validation_error", exception.getApiErrorCode());
        assertTrue(exception.getApiErrorMessage().contains("type 'invalid_type' not found"));
        assertEquals("write", exception.getOperationName());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testWriteValidationErrorWithInvalidRelation() throws Exception {
        // Try to write a tuple with valid type but invalid relation
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("invalid_relation")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());
        // Verify the formatted message includes operation name and API error details
        assertTrue(exception.getMessage().contains("write"));
        assertTrue(exception.getMessage().contains("relation 'document#invalid_relation' not found"));
        assertEquals("validation_error", exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertEquals("write", exception.getOperationName());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testErrorMessageFormattingWithAllFields() throws Exception {
        // Verify that when all fields are present, the message is properly formatted
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        // Verify the message follows the format: [operationName] apiErrorMessage (apiErrorCode)
        String message = exception.getMessage();
        assertTrue(message.startsWith("[write]"), "Message should start with [write]");
        assertTrue(message.contains("type 'invalid_type' not found"), "Message should contain the API error message");
        assertTrue(message.endsWith("(validation_error)"), "Message should end with (validation_error)");

        // Verify individual fields are accessible
        assertEquals("write", exception.getOperationName());
        assertEquals("validation_error", exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testCheckValidationError() throws Exception {
        // Test error handling for check operation to verify operation name is set correctly
        var checkRequest = new dev.openfga.sdk.api.client.model.ClientCheckRequest()
                ._object("invalid_type:readme")
                .relation("viewer")
                .user("user:anne");

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.check(checkRequest).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        // Verify operation name is "check" not "write"
        assertEquals("check", exception.getOperationName());
        assertTrue(exception.getMessage().contains("[check]"));
        assertEquals("validation_error", exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testErrorDetailsAreNotLostInStackTrace() throws Exception {
        // Verify that error details are preserved when exception is thrown
        // This addresses the issue where details were "buried down in the exception stack"
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        try {
            fga.write(request).get();
            fail("Expected ExecutionException to be thrown");
        } catch (ExecutionException e) {
            FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, e.getCause());

            // Verify that calling toString() or getMessage() gives useful information
            String errorString = exception.toString();
            assertTrue(
                    errorString.contains("type 'invalid_type' not found"),
                    "toString() should contain the API error message");

            String errorMessage = exception.getMessage();
            assertTrue(errorMessage.contains("[write]"), "getMessage() should contain operation name");
            assertTrue(errorMessage.contains("validation_error"), "getMessage() should contain error code");

            // Verify the response body is still accessible for custom parsing if needed
            assertNotNull(exception.getResponseData(), "Response body should be available");
        }
    }

    @Test
    public void testMultipleTupleErrorsShowDetailedMessage() throws Exception {
        // Test that validation errors for multiple tuples still show useful information
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey()
                                ._object("invalid_type1:readme")
                                .relation("viewer")
                                .user("user:anne"),
                        new ClientTupleKey()
                                ._object("invalid_type2:readme")
                                .relation("viewer")
                                .user("user:bob")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        // The error message should be informative even for batch operations
        assertTrue(exception.getMessage().contains("write"));
        assertEquals("validation_error", exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }
}
