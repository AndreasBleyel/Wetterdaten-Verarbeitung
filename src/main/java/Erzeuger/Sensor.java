package Erzeuger;

import Commons.SensorDaten;

/**
 * Ein Sensor welche Daten über die Methode produziere() liefert.
 */
public class Sensor {

  public SensorDaten produziere() {
    return new SensorDaten();
  }
}
