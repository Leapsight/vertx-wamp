package leapsight.vertxwamp.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.vertx.micrometer.backends.BackendRegistries;

public class Metrics {

    private final MeterRegistry registry;

    private static Metrics metrics;

    public static Metrics getInstance() {
        if (metrics == null) {
            synchronized (Metrics.class) {
                if (metrics == null) {
                    metrics = new Metrics("system-metrics");
                }
            }
        }

        return metrics;
    }

    private Metrics(String registryName) {
        this.registry = BackendRegistries.getNow(registryName);
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
    }

    public Counter getCounter(String counterName) {
        return registry.counter(counterName);
    }

}
