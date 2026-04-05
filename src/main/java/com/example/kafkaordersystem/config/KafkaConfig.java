package com.example.kafkaordersystem.config;

import com.example.kafkaordersystem.model.Order;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Configuration Class
 *
 * This class configures Kafka producer and consumer factories.
 * It handles serialization, deserialization, and topic creation.
 *
 * @EnableKafka: Enables Kafka annotations like @KafkaListener
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic.name:orders}")
    private String orderTopic;

    @Value("${spring.kafka.topic.partitions:3}")
    private int partitions;

    @Value("${spring.kafka.topic.replication-factor:1}")
    private short replicationFactor;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * KafkaAdmin Bean - used to create topics in Kafka
     */
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Create "orders" topic if it doesn't exist
     * This bean will be automatically called on application startup
     */
    @Bean
    public NewTopic ordersTopic() {
        return new NewTopic(orderTopic, partitions, replicationFactor);
    }

    /**
     * Producer Factory Configuration
     * Configures KafkaTemplate for sending messages to Kafka
     *
     * Uses JsonSerializer to serialize Order objects to JSON
     */
    @Bean
    public ProducerFactory<String, Order> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Ensure all replicas acknowledge the message
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // Number of retries if something goes wrong
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        // Batching configuration for performance
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate Bean
     * Used to send messages to Kafka topics
     */
    @Bean
    public KafkaTemplate<String, Order> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Consumer Factory Configuration
     * Configures the consumer to deserialize JSON messages to Order objects
     */
    @Bean
    public ConsumerFactory<String, Order> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put("spring.json.trusted.packages", "*");
        configProps.put("group.id", groupId);
        configProps.put("auto.offset.reset", "earliest");
        configProps.put("enable.auto.commit", true);
        configProps.put("max.poll.records", 100);

        return new DefaultKafkaConsumerFactory<>(
            configProps,
            new StringDeserializer(),
            new JsonDeserializer<>(Order.class, false)
        );
    }

    /**
     * Kafka Listener Container Factory
     * Configures the listener container for consuming messages concurrently
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Order>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Order> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConcurrency(3); // Process 3 messages concurrently
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
