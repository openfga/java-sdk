package dev.openfga.sdk.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import dev.openfga.sdk.api.configuration.Configuration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class TelemetryTest {
    @Test
    void shouldBeASingletonMetricsInitialization() {
        // given
        Telemetry telemetry = new Telemetry(new Configuration());

        // when
        Metrics firstCall = telemetry.metrics();
        Metrics secondCall = telemetry.metrics();

        // then
        assertThat(firstCall).isNotNull().isSameAs(secondCall);
    }

    @Test
    void shouldReturnSameMetricsInstanceUnderConcurrentAccess() throws InterruptedException {
        // given
        Telemetry telemetry = new Telemetry(new Configuration());
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Metrics> results = new CopyOnWriteArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    results.add(telemetry.metrics());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);

        // then
        assertThat(results).hasSize(threadCount);
        Metrics expected = results.get(0);
        assertThat(results).allSatisfy(m -> assertThat(m).isSameAs(expected));
    }
}
