package dev.openfga.sdk.example;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.openfga.OpenFGAContainer;

@Testcontainers
public class ExampleTest {
    @Container
    private static final OpenFGAContainer openfga = new OpenFGAContainer("openfga/openfga:v1.5.1");

    private final Example1 example1 = new Example1();

    @Test
    public void example1() throws Exception {

        example1.module = "test-integration";
        example1.run(openfga.getHttpEndpoint());
    }
}
