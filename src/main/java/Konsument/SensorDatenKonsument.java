package Konsument;

import Commons.SensorDaten;
import Commons.TestKonfiguration;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.util.Collections;
import java.util.Properties;

public class SensorDatenKonsument implements Runnable {

  /** Kafka Konsument */
  private final KafkaConsumer<Long, SensorDaten> konsument;

  /** Einrichtung der Verbindung zu Cassandra */
  private final Cluster cluster =
      Cluster.builder().addContactPoint(TestKonfiguration.CASS_SERVER_IP).build();

  private final Session session = cluster.connect(TestKonfiguration.KEYSPACE);

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
  public void run() {

    try {
      konsument.subscribe(Collections.singletonList(TestKonfiguration.TOPIC));
      PreparedStatement prepStmt =
          session.prepare(
              "INSERT INTO wetterdaten (id, date_time, air_temp, std_air_temp, humidity, std_humidity,"
                  + "IR_temp, std_IR_temp, air_pressure, std_pressure, wind_speed, std_wind_speed, light_A,"
                  + "std_light_A, light_B, std_light_B, distance, std_distance, counter, roll, pitch,"
                  + "X_accel, std_X_accel, Y_accel, std_Y_accel, Z_accel, std_Z_accel, battery, error,"
                  + "WDT_trace, crc3) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      while (true) {
        ConsumerRecords<Long, SensorDaten> kafkaRecord = konsument.poll(Long.MAX_VALUE);
        System.out.println("*** Poll ***");

        kafkaRecord.forEach(
            datum -> {
              session.execute(
                  prepStmt.bind(
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

              System.out.printf(
                  "Write erfolgreich:(%d, %s, %d, %d)\n",
                  datum.key(), datum.value(), datum.partition(), datum.offset());

            });
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cluster.close();
      session.close();
      konsument.close();
    }
  }
}
