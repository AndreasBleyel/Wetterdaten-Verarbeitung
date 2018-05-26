import Erzeuger.SensorDataProduzent;
import Konsument.SensorDatenKonsument;

public class Main {

  public static void main(String... args) {

    long startTime = System.currentTimeMillis();

    SensorDatenKonsument konsument = new SensorDatenKonsument();

    Thread threadKonsument = new Thread(konsument, "Thread Konsument");
    Thread threadProduzent = new Thread(new SensorDataProduzent(), "Thread Produzent");

    threadKonsument.start();
    threadProduzent.start();

    if (!threadProduzent.isAlive()) konsument.stop();

    long stopTime = System.currentTimeMillis();

    System.out.format("Benötigte Zeit: %d", stopTime - startTime);
  }
}
