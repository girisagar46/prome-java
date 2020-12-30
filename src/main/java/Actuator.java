import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class Actuator {

  private final PrometheusMeterRegistry prometheusMeterRegistry;
  private HttpServer server;

  public Actuator(
      PrometheusMeterRegistry prometheusMeterRegistry) {
    // inject PrometheusMeterRegistry to Actuator class because we're using Prometheus
    this.prometheusMeterRegistry = prometheusMeterRegistry;

    // These classes are for exposing JVM specific metrics
    new ClassLoaderMetrics().bindTo(this.prometheusMeterRegistry);
    new JvmMemoryMetrics().bindTo(this.prometheusMeterRegistry);
    new JvmGcMetrics().bindTo(this.prometheusMeterRegistry);
    new ProcessorMetrics().bindTo(this.prometheusMeterRegistry);
    new JvmThreadMetrics().bindTo(this.prometheusMeterRegistry);
  }

  public void start() throws IOException {
    // create HTTP server endpoint which will expose endpoint to /metrics
    server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(2));

    server.createContext(
        "/metrics",
        httpExchange -> {
          String response = this.prometheusMeterRegistry.scrape();
          httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
          try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
          }
        });

    server.start();
  }

  public void stop() {
    server.stop(0);
  }
}
