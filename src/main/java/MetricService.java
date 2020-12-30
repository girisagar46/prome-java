import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MetricService {

  private static PrometheusMeterRegistry prometheusMeterRegistry = null;

  public static PrometheusMeterRegistry getRegistry() {
    if (prometheusMeterRegistry == null) {
      prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
    return prometheusMeterRegistry;
  }
}
