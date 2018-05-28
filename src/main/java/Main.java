import Erzeuger.SensorDatenProduzent;
import Konsument.SensorDatenKonsument;

public class Main {

  public static void main(String... args) {

    SensorDatenKonsument konsument = new SensorDatenKonsument("SensorDatenKonsument");
    Thread threadKonsument = new Thread(konsument, "Thread Konsument");

    SensorDatenProduzent produzent = new SensorDatenProduzent( "SensorDatenProduzent");
    Thread threadProduzent = new Thread(produzent, "Thread Produzent");

    threadKonsument.start();
    threadProduzent.start();
  }
}
