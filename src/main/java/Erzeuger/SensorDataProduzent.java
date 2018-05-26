package Erzeuger;

import Commons.SensorDaten;
import Commons.ServerKonfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;

public class SensorDataProduzent implements Runnable{

    private static final int ANZAHL_NACHRICHTEN = 500;
    private static final int INTERVALL_SEKUNDEN = 5;

    private void produziere(){

        Sensor sensor1 = new Sensor("S1");

        long time = System.currentTimeMillis();

        Producer<Long, SensorDaten> producer = erzeugeKafkaProduzent();

        try{
        for (long index = time; index < time + ANZAHL_NACHRICHTEN; index++) {
                ProducerRecord<Long, SensorDaten> record = new ProducerRecord<>(ServerKonfiguration.TOPIC, index, sensor1.produziere());
                producer.send(record);
                //Thread.sleep(INTERVALL_SEKUNDEN*1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            producer.flush();
            producer.close();
        }

    }

    private Producer<Long, SensorDaten> erzeugeKafkaProduzent() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ServerKonfiguration.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "SensorDatenProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorDatenSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Override
    public void run() {
        produziere();
    }
}
