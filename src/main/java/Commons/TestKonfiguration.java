package Commons;

/**
 * Nicht instanziierbare Klasse welche diverse Konfigurationsvariablen enthält.
 */
public class TestKonfiguration {

  /**
   * Kafka
   */
  public static final String TOPIC = "wetterdaten";
  public static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

  /**
   * SensorDatenProduzent
   */
  public static int ANZAHL_NACHRICHTEN = 10;
  public static int ANZAHL_PRODUZENTEN = 4;

  /**
   * Cassandra
   */
  public static final String KEYSPACE = "ba2";
  public static final String CASS_SERVER_IP = "127.0.0.1";
  public static final String WETTERDATEN_TABLE = "wetterdaten";

  private TestKonfiguration() {
    }
}
