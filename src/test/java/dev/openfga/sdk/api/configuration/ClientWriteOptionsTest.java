package dev.openfga.sdk.api.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClientWriteOptionsTest {

    @Test
    void transactionsEnabledByDefault() {
        ClientWriteOptions options = new ClientWriteOptions();

        assertTrue(options.isTransactionsEnabled());
    }

    @Test
    void transactionsFalseDisablesTransactions() {
        ClientWriteOptions options = new ClientWriteOptions().transactions(false);

        assertFalse(options.isTransactionsEnabled());
    }

    /**
     * Covers the deprecated {@code disableTransactions} methods and asserts they remain the exact
     * inverse of {@code transactions}/{@code isTransactionsEnabled}. This is the one place the
     * deprecated path is exercised on purpose; the suppression keeps the rest of the build
     * warning-free. Remove this test when the deprecated methods are removed.
     */
    @Test
    @SuppressWarnings("deprecation")
    void deprecatedDisableTransactionsRemainsInverseOfTransactions() {
        // transactions(false) implies disableTransactions() == true
        assertTrue(new ClientWriteOptions().transactions(false).disableTransactions());

        // transactions(true) implies disableTransactions() == false
        assertFalse(new ClientWriteOptions().transactions(true).disableTransactions());

        // disableTransactions(true) implies isTransactionsEnabled() == false
        assertFalse(new ClientWriteOptions().disableTransactions(true).isTransactionsEnabled());

        // disableTransactions(false) implies isTransactionsEnabled() == true
        assertTrue(new ClientWriteOptions().disableTransactions(false).isTransactionsEnabled());
    }
}
