package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestAttemptBodyLoggerTest {
    private static String logBody(String body, boolean formEncoded) {
        return log(HttpRequest.BodyPublishers.ofString(body), formEncoded);
    }

    private static String log(HttpRequest.BodyPublisher publisher, boolean formEncoded) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        publisher.subscribe(new HttpRequestAttempt.BodyLogger(
                new PrintStream(out, true, StandardCharsets.UTF_8), "request", formEncoded));
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    void redactsFormFieldsOtherThanTheLoggedOnes() {
        String output = logBody(
                "client_id=my-client&client_secret=my-secret&audience=my-audience&grant_type=client_credentials", true);

        assertFalse(output.contains("my-secret"), output);
        assertTrue(
                output.contains(
                        "request: client_id=my-client&client_secret=[REDACTED]&audience=my-audience&grant_type=client_credentials"),
                output);
    }

    @Test
    void redactsUnknownFormFields() {
        String output = logBody("grant_type=client_credentials&client_assertion=my-assertion", true);

        assertFalse(output.contains("my-assertion"), output);
        assertTrue(output.contains("request: grant_type=client_credentials&client_assertion=[REDACTED]"), output);
    }

    @Test
    void logsJsonBodiesUnchanged() {
        String body =
                "{\"tuple_key\":{\"user\":\"user:anne\",\"relation\":\"viewer\",\"object\":\"document:roadmap\"}}";

        String output = logBody(body, false);

        assertTrue(output.contains("request: " + body), output);
    }

    @Test
    void logsNothingForEmptyBodies() {
        String output = log(HttpRequest.BodyPublishers.noBody(), false);

        assertFalse(output.contains("request:"), output);
    }
}
