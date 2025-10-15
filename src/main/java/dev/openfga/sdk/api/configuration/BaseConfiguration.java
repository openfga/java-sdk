package dev.openfga.sdk.api.configuration;

import java.time.Duration;

public interface BaseConfiguration {
    String getApiUrl();

    String getUserAgent();

    Duration getReadTimeout();

    Duration getConnectTimeout();

    Integer getMaxRetries();

    Duration getMinimumRetryDelay();
}
