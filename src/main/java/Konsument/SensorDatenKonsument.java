package Konsument;

import Commons.SensorDaten;
import Commons.ServerKonfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.util.Collections;
import java.util.Properties;

public class SensorDatenKonsument implements Runnable {

  private final KafkaConsumer<Long, SensorDaten> konsument;
  private boolean running;

  public SensorDatenKonsument(String groupId) {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ServerKonfiguration.BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorDatenDeserializer.class.getName());
    this.konsument = new KafkaConsumer<Long, SensorDaten>(props);
  }

  @Override
  public void run() {

    setRunning(true);

    try {
      CassandraConnector cassandraConnector = new CassandraConnector();
      konsument.subscribe(Collections.singletonList(ServerKonfiguration.TOPIC));

      while (running) {
        ConsumerRecords<Long, SensorDaten> sensorDaten = konsument.poll(Long.MAX_VALUE);
        sensorDaten.forEach(
                datum -> {
                  cassandraConnector.schreibeSensorDaten(datum.key(), datum.value());
                  System.out.printf(
                          "Consumer Record:(%d, %s, %d, %d)\n",
                          datum.key(), datum.value(), datum.partition(), datum.offset());
                });
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      konsument.close();
    }
  }

  public void stop() {
    setRunning(false);
  }

  private void setRunning(boolean running) {
    this.running = running;
  }
}
