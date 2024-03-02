package ru.edi.edi_integration.cfg.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${project.kafka.server}")
    private String kafkaServer;

    @Value("${project.kafka.client.id}")
    private String clientId;

    @Bean
    public Map<String, Object> producerConfigurations() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId + "-" + UUID.randomUUID().toString());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "720000");
        log.info("Created config producerConfigurations");
        return props;
    }

    @Bean
    public DefaultKafkaProducerFactory<String, String> producerFactoryMessage() {
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerConfigurations());
        producerFactory.setTransactionIdPrefix("transaction.prefix." + UUID.randomUUID().toString() + ".");//Зачем?
        log.info("Created  producerFactoryMessage");

        return producerFactory;
    }

    @Bean("tmKafkaMessage")
    public KafkaTransactionManager<String, String> kafkaTransactionManagerMessage(DefaultKafkaProducerFactory<String, String> producerFactory) {
        KafkaTransactionManager<String, String> transactionManager = new KafkaTransactionManager<String, String>(producerFactory);
        transactionManager.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        log.info("Created transactionManager tmKafkaMessage");
        return transactionManager;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateMessage(DefaultKafkaProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
        log.info("Created producerFactory)");
        return template;
    }
}