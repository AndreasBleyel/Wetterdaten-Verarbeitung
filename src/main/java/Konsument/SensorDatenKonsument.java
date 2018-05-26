package Konsument;

import Commons.SensorDaten;
import Commons.ServerKonfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.util.Collections;
import java.util.Properties;

public class SensorDatenKonsument implements Runnable{

  private boolean running = true;

  private void konsumiere() {
    KafkaConsumer<Long, SensorDaten> konsument = erzeugeKafkaKonsument();

    try {
      konsument.subscribe(Collections.singletonList(ServerKonfiguration.TOPIC));
      while (running) {
        ConsumerRecords<Long, SensorDaten> sensorDaten = konsument.poll(100);
        sensorDaten.forEach(
            datum -> {
              CassandraConnector.schreibeSensorDaten(datum.key(), datum.value());
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

  private KafkaConsumer<Long, SensorDaten> erzeugeKafkaKonsument() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ServerKonfiguration.BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "SensorDatenConsumer");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorDatenDeserializer.class.getName());
    return new KafkaConsumer<Long, SensorDaten>(props);
  }

  @Override
  public void run() {
    konsumiere();
  }

  public void stop(){
    setRunning(false);
  }

  private void setRunning(boolean running) {
    this.running = running;
  }
}
