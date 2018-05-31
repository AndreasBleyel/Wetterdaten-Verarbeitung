package Erzeuger;

import Commons.NotifyingThread;
import Commons.SensorDaten;
import Commons.TestKonfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SensorDatenProduzent übernimmt die Generierung der Sensordaten. Jede Instanz verbindet sich mit
 * der Kafka Instanz und erzeut mittels eines Sensors Daten. Die Anzahl hängt von dem in der Klasse
 * TestKonfiguration.class definierten Wertes ab.
 */
public class SensorDatenProduzent extends NotifyingThread {

  private final Producer<Long, SensorDaten> produzent;

  public SensorDatenProduzent(String clientId) {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, TestKonfiguration.BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorDatenSerializer.class.getName());
    this.produzent = new KafkaProducer<>(props);
  }

  @Override
  public void doRun() {
    Sensor sensor1 = new Sensor();
    try {
      for (int i = 0; i < TestKonfiguration.ANZAHL_NACHRICHTEN; i++) {
        ProducerRecord<Long, SensorDaten> record =
            new ProducerRecord<>(
                TestKonfiguration.TOPIC,
                ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
                sensor1.produziere());
        produzent.send(record);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      produzent.flush();
      produzent.close();
    }
  }
}
