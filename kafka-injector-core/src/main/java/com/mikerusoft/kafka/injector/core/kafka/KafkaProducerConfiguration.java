package com.mikerusoft.kafka.injector.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mikerusoft.kafka.injector.core.properties.Kafka;
import com.mikerusoft.kafka.injector.core.properties.KafkaProperties;
import com.mikerusoft.kafka.injector.core.properties.Topic;
import com.mikerusoft.kafka.injector.core.utils.Pair;
import com.mikerusoft.kafka.injector.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class KafkaProducerConfiguration {

    private static final String TOPIC_PARAM_NAME = "kafka.topic";

    private static final KafkaProducerConfiguration instance = init();

    private static KafkaProducerConfiguration init() {
        String kafkaInjectorConf = System.getProperty("kafkaInjectorConf");
        boolean isEmptyConf = Utils.isEmpty(kafkaInjectorConf);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Kafka kafkaProperties =
                isEmptyConf ?
                    mapper.readValue(ClassLoader.getSystemResource("kafka.yml"), KafkaProperties.class).getKafka():
                    mapper.readValue(new File(kafkaInjectorConf), KafkaProperties.class).getKafka();
            Map<String, KafkaProducer<?, ?>> producers = Stream.of(kafkaProperties.getTopics())
                .map(kp -> KafkaProducerConfiguration.createKafkaProducerProps(kafkaProperties, kp, kafkaProperties.getUrl()))
                .distinct()
                .collect(Collectors.toMap(
                    properties -> (String)properties.get(TOPIC_PARAM_NAME),
                    KafkaProducer::new,
                    (k1, k2) -> k1)
                );
            List<Topic> topics = Stream.of(kafkaProperties.getTopics()).collect(Collectors.toList());
            if (kafkaProperties.isCreateTopics()) {
                createTopicsIfDoesNotExist(kafkaProperties.getUrl(), topics);
            }
            return new KafkaProducerConfiguration(
                Collections.unmodifiableMap(producers),
                Collections.unmodifiableList(topics)
            );
        } catch (IOException e) {
            Utils.rethrowRuntimeException(e);
        }
        return null;
    }

    private static void createTopicsIfDoesNotExist(String brokers, List<Topic> topics) {
        try {
            Properties props = new Properties();
            props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            AdminClient adminClient = KafkaAdminClient.create(props);

            Set<String> existedTopics = adminClient.listTopics().names().get(5, TimeUnit.SECONDS);
            topics.stream().filter(t -> !existedTopics.contains(t.getName())).forEach(topic -> {
                try {
                    adminClient.createTopics(
                        Collections.singletonList(new NewTopic(topic.getName(), Optional.ofNullable(topic.getPartitions()).orElse(1), Optional.ofNullable(topic.getReplicas()).orElse((short) 0)))
                    ).all().get(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create topic " + topic.getName(), e);
                }
            });
        } catch (Exception e) {
            Utils.rethrowRuntimeException(e);
        }
    }

    private static Properties createKafkaProducerProps(Kafka kafkaProperties, Topic topic, String url) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, topic.getClientIdConfig());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, topic.getSerializer().getValue());
        String keySerializer = topic.getSerializer().getKey();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);

        if (!Utils.isEmpty(kafkaProperties.getSchemaRegistryUrl())) {
            props.put("schema.registry.url", kafkaProperties.getSchemaRegistryUrl());
        }

        props.put(TOPIC_PARAM_NAME, topic.getName());
        props.put(ProducerConfig.ACKS_CONFIG, String.valueOf(-1));
        props.put(ProducerConfig.LINGER_MS_CONFIG, String.valueOf(kafkaProperties.getLingerMs()));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(kafkaProperties.getBatchSize()));
        return props;
    }

    private final Map<String, KafkaProducer<?, ?>> producers;
    private final List<Topic> topics;

    private KafkaProducerConfiguration(Map<String, KafkaProducer<?, ?>> producers, List<Topic> topics) {
        this.producers = producers;
        this.topics = topics;
    }

    public static List<Topic> getTopics() {
        return instance.topics;
    }

    public static void insertIntoKafka(String topicName, List<Pair<Object, Object>> dataToInject) {
        KafkaProducer<?, ?> producer = instance.producers.get(topicName);
        log.debug("Sending to Kafka " + dataToInject.size() + " dataToInject");
        dataToInject.forEach(pair ->
                producer.send(new ProducerRecord(topicName, pair.getLeft(), pair.getRight()), KafkaProducerConfiguration::printException)
        );
    }

    private static void printException(RecordMetadata rm, Exception exception) {
        if (exception != null) {
            log.error(exception.getMessage(), exception);
        }
    }

    public static void closeAll() {
        instance.producers.values().forEach(p -> {
            try {
                p.close();
            } catch (Exception e) {
                log.warn("Failed to close producer {}", e.getMessage());
            }
        });
    }

}
