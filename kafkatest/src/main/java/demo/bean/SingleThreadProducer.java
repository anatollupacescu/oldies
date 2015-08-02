package demo.bean;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import kafka.producer.KeyedMessage;

public class SingleThreadProducer {

	private @Autowired Producer<Integer, String> producer;

	private @Autowired kafka.javaapi.producer.Producer<Integer, String> kafkaProducer;

	public void sendMessage(final String topic, final String message) {
		final ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, message);
		producer.send(record);
	}

	public void sendMessage(final String topic, final String message, boolean go) {
		KeyedMessage<Integer, String> keyedMessage = new KeyedMessage<>(topic, message);
		kafkaProducer.send(keyedMessage);
	}

	public void shutdown() {
		((KafkaProducer<Integer, String>) producer).close();
	}
}
