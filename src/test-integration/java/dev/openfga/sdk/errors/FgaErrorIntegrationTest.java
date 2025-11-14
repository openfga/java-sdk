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

    @Test
    public void testNotFoundError() throws Exception {
        ClientConfiguration badConfig = new ClientConfiguration()
                .apiUrl(openfga.getHttpEndpoint())
                .storeId("01HVJPQR3TXYZ9NQXABCDEFGHI"); // Non-existent store ID
        OpenFgaClient badClient = new OpenFgaClient(badConfig);

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            badClient.getStore().get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("getStore")
                || exception.getMessage().contains("[get"));
        assertNotNull(exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testInvalidParameterException() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest(); // Empty request, no writes or deletes

        // This should throw FgaInvalidParameterException before making the API call
        dev.openfga.sdk.errors.FgaInvalidParameterException exception =
                assertThrows(dev.openfga.sdk.errors.FgaInvalidParameterException.class, () -> {
                    // Trying to write with null storeId by creating a new client without storeId
                    ClientConfiguration config = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
                    OpenFgaClient client = new OpenFgaClient(config);
                    client.write(request).get();
                });

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("storeId")
                || exception.getMessage().contains("parameter"));
    }

    @Test
    public void testAuthenticationError() throws Exception {
        // Test FgaApiAuthenticationError when store doesn't exist (403/401 from API)
        // Use a non-existent store ID to trigger authentication/authorization error
        ClientConfiguration badConfig =
                new ClientConfiguration().apiUrl(openfga.getHttpEndpoint()).storeId("non-existent-store-id-12345");
        OpenFgaClient badClient = new OpenFgaClient(badConfig);

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            badClient.write(request).get();
        });

        // Could be NotFoundError (404) or AuthenticationError depending on API version
        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        // Verify error details are populated
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("[write]")
                || exception.getMessage().contains("write"));
        assertNotNull(exception.getApiErrorCode());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testReadOperationError() throws Exception {
        // Test that read operations also surface error details correctly
        // Read requires both user and object when object is a type prefix
        var readRequest = new dev.openfga.sdk.api.client.model.ClientReadRequest()
                ._object("invalid_type:"); // Type prefix without user will fail

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.read(readRequest).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        // Verify operation name is "read"
        assertTrue(exception.getOperationName().equals("read")
                || exception.getMessage().contains("read"));
        assertEquals("validation_error", exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testExpandOperationError() throws Exception {
        // Test that expand operations surface error details correctly
        var expandRequest = new dev.openfga.sdk.api.client.model.ClientExpandRequest()
                ._object("invalid_type:readme")
                .relation("viewer");

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.expand(expandRequest).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        // Verify operation name is "expand"
        assertTrue(exception.getOperationName().equals("expand")
                || exception.getMessage().contains("expand"));
        assertEquals("validation_error", exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void testDifferentErrorCodesAreSurfaced() throws Exception {
        // Test that different validation_error subcases are properly surfaced
        // Case 1: Invalid type
        ClientWriteRequest request1 = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException ex1 =
                assertThrows(ExecutionException.class, () -> fga.write(request1).get());
        FgaApiValidationError error1 = assertInstanceOf(FgaApiValidationError.class, ex1.getCause());
        assertTrue(error1.getApiErrorMessage().contains("type"));

        // Case 2: Invalid relation
        ClientWriteRequest request2 = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("invalid_relation")
                        .user("user:anne")));

        ExecutionException ex2 =
                assertThrows(ExecutionException.class, () -> fga.write(request2).get());
        FgaApiValidationError error2 = assertInstanceOf(FgaApiValidationError.class, ex2.getCause());
        assertTrue(error2.getApiErrorMessage().contains("relation"));

        // Both should have the same error code but different messages
        assertEquals(error1.getApiErrorCode(), error2.getApiErrorCode());
        assertNotEquals(error1.getApiErrorMessage(), error2.getApiErrorMessage());
    }

    @Test
    public void testErrorMessageContainsOperationContext() throws Exception {
        // Verify that different operations have their names in the error message

        ClientWriteRequest writeReq = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey()._object("invalid:x").relation("r").user("user:x")));

        ExecutionException writeEx =
                assertThrows(ExecutionException.class, () -> fga.write(writeReq).get());
        FgaError writeError = assertInstanceOf(FgaError.class, writeEx.getCause());
        assertTrue(writeError.getMessage().contains("[write]"), "Write error should contain [write] in message");

        // Check operation
        var checkReq = new dev.openfga.sdk.api.client.model.ClientCheckRequest()
                ._object("invalid:x")
                .relation("r")
                .user("user:x");

        ExecutionException checkEx =
                assertThrows(ExecutionException.class, () -> fga.check(checkReq).get());
        FgaError checkError = assertInstanceOf(FgaError.class, checkEx.getCause());
        assertTrue(checkError.getMessage().contains("[check]"), "Check error should contain [check] in message");

        // Verify they have different operation names
        assertNotEquals(writeError.getOperationName(), checkError.getOperationName());
    }
}
