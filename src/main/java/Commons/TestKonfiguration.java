package Commons;

public class TestKonfiguration {

  /**
   * Kafka
   */
  public static final String TOPIC = "sensordaten";
  public static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

  /**
   * SensorDatenProduzent
   */
  public static int ANZAHL_NACHRICHTEN = 50;

  /**
   * Cassandra
   */
  public static final String KEYSPACE = "ba2";
  public static final String CASS_SERVER_IP = "127.0.0.1";

  private TestKonfiguration() {
    }
}
