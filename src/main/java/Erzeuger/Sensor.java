package Erzeuger;

import Commons.SensorDaten;

public class Sensor {

  private String name;

  public Sensor(String name) {
    this.name = name;
  }

  public SensorDaten produziere() {
    return new SensorDaten();
  }
}
