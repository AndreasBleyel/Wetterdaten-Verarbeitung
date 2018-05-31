package Commons;

import Erzeuger.SensorDatenProduzent;
import Konsument.SensorDatenKonsument;
import org.slf4j.Logger;

/**
 * ThreadHandler übernimmt die Koordination zwischen den einzelnen Thread. Dies ist notwendig, um
 * die Laufzeit der Datenverarbeitung zu messen.
 */
public class ThreadHandler implements ThreadCompleteListener {

  private int anzahlBeendeterThreads = 0;
  private SensorDatenKonsument konsument;


  public void startThreads() {
    konsument = new SensorDatenKonsument("SensorDatenKonsument");
    NotifyingThread threadKonsument = konsument;

    SensorDatenProduzent produzent1 = new SensorDatenProduzent("SensorDatenProduzent1");
    NotifyingThread threadProduzent1 = produzent1;

    SensorDatenProduzent produzent2 = new SensorDatenProduzent("SensorDatenProduzent2");
    NotifyingThread threadProduzent2 = produzent2;

    SensorDatenProduzent produzent3 = new SensorDatenProduzent("SensorDatenProduzent3");
    NotifyingThread threadProduzent3 = produzent3;

    SensorDatenProduzent produzent4 = new SensorDatenProduzent("SensorDatenProduzent4");
    NotifyingThread threadProduzent4 = produzent4;

    threadProduzent1.addListener(this);
    threadProduzent2.addListener(this);
    threadProduzent3.addListener(this);
    threadProduzent4.addListener(this);
    threadKonsument.addListener(this);

    long startTime = System.currentTimeMillis();

    threadProduzent1.start();
    threadProduzent2.start();
    threadProduzent3.start();
    threadProduzent4.start();

    try {
      threadProduzent1.join();
      threadProduzent2.join();
      threadProduzent3.join();
      threadProduzent4.join();
      threadKonsument.start();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


    try{
      threadKonsument.join();
    }catch (InterruptedException e){
      e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();

    long gesamtLaufzeit = endTime - startTime;

    System.out.println("Laufzeit: " + gesamtLaufzeit / 1000 + "s");
  }

  @Override
  public void notifyOfThreadComplete(Thread thread) {
    /**
     * Sobald alle Produzenten fertig sind, wird der Konsument darüber benachrichtigt um beendet zu
     * werden.
     */
    anzahlBeendeterThreads++;
    if (anzahlBeendeterThreads == TestKonfiguration.ANZAHL_PRODUZENTEN) {
      konsument.produzentenFertig();
    }
  }
}
