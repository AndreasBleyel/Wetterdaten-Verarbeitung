import Erzeuger.SensorDatenProduzent;
import Konsument.SensorDatenKonsument;

public class Main {

  public static void main(String... args) {

    SensorDatenKonsument konsument = new SensorDatenKonsument("SensorDatenKonsument");
    Thread threadKonsument = new Thread(konsument, "Thread Konsument");

    SensorDatenProduzent produzent1 = new SensorDatenProduzent("SensorDatenProduzent1","P1");
    Thread threadProduzent1 = new Thread(produzent1, "Thread Produzent1");

    SensorDatenProduzent produzent2 = new SensorDatenProduzent("SensorDatenProduzent2","P2");
    Thread threadProduzent2 = new Thread(produzent2, "Thread Produzent2");

    SensorDatenProduzent produzent3 = new SensorDatenProduzent("SensorDatenProduzent3","P3");
    Thread threadProduzent3 = new Thread(produzent3, "Thread Produzent3");

    SensorDatenProduzent produzent4 = new SensorDatenProduzent("SensorDatenProduzent4","P4");
    Thread threadProduzent4 = new Thread(produzent4, "Thread Produzent4");

    threadProduzent1.start();
    threadProduzent2.start();
    threadProduzent3.start();
    threadProduzent4.start();

    threadKonsument.start();
  }
}
