package dev.openfga.sdk.api.configuration;

import java.util.Map;

public interface AdditionalHeadersSupplier {
    Map<String, String> getAdditionalHeaders();
}
