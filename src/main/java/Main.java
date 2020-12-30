import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    Actuator actuator = new Actuator(MetricService.getRegistry());
    try {
      actuator.start();
    } catch (IOException ioException) {
      actuator.stop();
    }
  }
}
