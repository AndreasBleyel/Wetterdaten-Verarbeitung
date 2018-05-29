package Erzeuger;

import Commons.SensorDaten;
import Commons.TestKonfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class SensorDatenProduzent implements Runnable {

  private final Producer<Long, SensorDaten> produzent;
  private String name;

  public SensorDatenProduzent(String clientId, String name) {
    this.name = name;
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, TestKonfiguration.BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorDatenSerializer.class.getName());
    this.produzent = new KafkaProducer<Long, SensorDaten>(props);
  }

  @Override
  public void run() {
    Sensor sensor1 = new Sensor();
    try {
      for (int i = 0; i < TestKonfiguration.ANZAHL_NACHRICHTEN; i++) {
        ProducerRecord<Long, SensorDaten> record =
            new ProducerRecord<>(
                TestKonfiguration.TOPIC,
                ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
                sensor1.produziere());
        produzent.send(record);
        System.out.printf("#%s#", name);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      produzent.flush();
      produzent.close();
    }
  }
}
