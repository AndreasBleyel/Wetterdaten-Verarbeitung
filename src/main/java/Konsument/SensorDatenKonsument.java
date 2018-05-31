package Konsument;

import Commons.NotifyingThread;
import Commons.SensorDaten;
import Commons.TestKonfiguration;
import com.datastax.driver.core.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.util.Collections;
import java.util.Properties;

/**
 * SensorDatenKonsument bildet einen Kafka Konsumenten ab welcher die Daten aus dem Kafka Topic
 * liest, deserialisiert und in die Cassandra Datenbank schreibt.
 */
public class SensorDatenKonsument extends NotifyingThread {

  /** Kafka Konsument */
  private final KafkaConsumer<Long, SensorDaten> konsument;

  /** Einrichtung der Verbindung zu Cassandra */
  private final Cluster cluster =
      Cluster.builder().addContactPoint(TestKonfiguration.CASS_SERVER_IP).build();

  private final Session session = cluster.connect(TestKonfiguration.KEYSPACE);

  private volatile boolean produzentenAktiv = true;
  private int sumOfRowsAtStart;

  public SensorDatenKonsument(String groupId) {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestKonfiguration.BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorDatenDeserializer.class.getName());
    this.konsument = new KafkaConsumer<>(props);
  }

  @Override
  public void doRun() {

    try {
      konsument.subscribe(Collections.singletonList(TestKonfiguration.TOPIC));
      PreparedStatement insertDataStmt =
          session.prepare(
              "INSERT INTO "
                  + TestKonfiguration.WETTERDATEN_TABLE
                  + " (id, date_time, air_temp, std_air_temp, humidity, std_humidity,"
                  + "IR_temp, std_IR_temp, air_pressure, std_pressure, wind_speed, std_wind_speed, light_A,"
                  + "std_light_A, light_B, std_light_B, distance, std_distance, counter, roll, pitch,"
                  + "X_accel, std_X_accel, Y_accel, std_Y_accel, Z_accel, std_Z_accel, battery, error,"
                  + "WDT_trace, crc3) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      getNumberOfRowsAtStart();

      /**
       * Solange es aktive Produzenten gibt oder es noch offene Datensätze zu schreiben gibt, ist
       * der Konsument aktiv.
       */
      while (produzentenAktiv || !isAllDataWritten()) {
        ConsumerRecords<Long, SensorDaten> kafkaRecord = konsument.poll(Long.MAX_VALUE);

        if (!kafkaRecord.isEmpty()) {
          kafkaRecord.forEach(
              datum -> {
                session.execute(
                    insertDataStmt.bind(
                        datum.key(),
                        datum.value().getDateTime(),
                        datum.value().getAirTemp(),
                        datum.value().getStdAirTemp(),
                        datum.value().getHumidity(),
                        datum.value().getStdHumidity(),
                        datum.value().getIrTemp(),
                        datum.value().getStdIrTemp(),
                        datum.value().getAirPressure(),
                        datum.value().getStdAirPressure(),
                        datum.value().getWindSpeed(),
                        datum.value().getStdWindSpeed(),
                        datum.value().getLightA(),
                        datum.value().getStdLightA(),
                        datum.value().getLightB(),
                        datum.value().getStdLightB(),
                        datum.value().getDistance(),
                        datum.value().getStdDistance(),
                        datum.value().getCounter(),
                        datum.value().getRoll(),
                        datum.value().getPitch(),
                        datum.value().getxAccel(),
                        datum.value().getStdXAccel(),
                        datum.value().getyAccel(),
                        datum.value().getStdYAccel(),
                        datum.value().getzAccel(),
                        datum.value().getStdZAccel(),
                        datum.value().getBattery(),
                        datum.value().getError(),
                        datum.value().getWdtTrace(),
                        datum.value().getCrc3()));
              });
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cluster.close();
      session.close();
      konsument.close();
    }
  }

  /**
   * Prüft ob alle Datensätze geschrieben sind. Dafür wird die Anzahl der bereits vor Beginn des
   * ersten Schreibvorganges in der Tabelle befindlichen Datensätze mit der zu schreibenden (vorher
   * bekannt) Anzahl verglichen. Dieser Vorgang ist nur notwendig um die Laufzeitmessung durchführen
   * zu können.
   */
  private boolean isAllDataWritten() {
    ResultSet resultSet =
        session.execute(
            "SELECT count(*) as rows FROM " + TestKonfiguration.WETTERDATEN_TABLE + " ;");

    int actualSumOfRows = convertResultToInt(resultSet.one().toString());
    if ((actualSumOfRows - sumOfRowsAtStart)
        == (TestKonfiguration.ANZAHL_NACHRICHTEN * TestKonfiguration.ANZAHL_PRODUZENTEN)) {
      return true;
    }
    return false;
  }

  /**
   * Liefert die vor Beginn des ersten Schreibvorgangens in der Tabelle befindliche Anzahl an
   * Datensätze.
   */
  private void getNumberOfRowsAtStart() {
    ResultSet resultSet =
        session.execute(
            "SELECT count(*) as rows FROM " + TestKonfiguration.WETTERDATEN_TABLE + " ;");
    sumOfRowsAtStart = convertResultToInt(resultSet.one().toString());
    System.out.println("Number of rows at start: " + sumOfRowsAtStart);
  }

  public void produzentenFertig() {
    produzentenAktiv = false;
  }

  private int convertResultToInt(String result) {
    return Integer.parseInt(result.substring(4, result.length() - 1));
  }
}
