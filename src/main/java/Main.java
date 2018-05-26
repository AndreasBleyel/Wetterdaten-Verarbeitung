import Erzeuger.SensorDataProduzent;
import Konsument.SensorDatenKonsument;

public class Main {

  public static void main(String... args) {

    long startTime = System.currentTimeMillis();

    SensorDatenKonsument konsument = new SensorDatenKonsument( "Konsument");
    SensorDataProduzent produzent = new SensorDataProduzent(500, "SensorDatenProducer");

    Thread threadKonsument = new Thread(konsument, "Thread Konsument");
    Thread threadProduzent = new Thread(produzent, "Thread Produzent");

      threadProduzent.start();
      threadKonsument.start();

    if (!threadProduzent.isAlive()) {
      konsument.stop();
      long stopTime = System.currentTimeMillis();
      System.out.format("Benötigte Zeit: %d", stopTime - startTime);
    }
  }
}
