package dev.openfga.sdk.example;

import org.junit.jupiter.api.Test;

public class ExampleTest {
    private final Example1 example1 = new Example1();

    @Test
    public void example1() throws Exception {
        example1.module = "test-integration";
        example1.run();
    }
}
