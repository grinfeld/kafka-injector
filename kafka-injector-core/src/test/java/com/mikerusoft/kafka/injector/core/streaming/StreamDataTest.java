package com.mikerusoft.kafka.injector.core.streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mikerusoft.kafka.injector.core.properties.Kafka;
import com.mikerusoft.kafka.injector.core.properties.KafkaProperties;
import com.mikerusoft.kafka.injector.core.utils.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class StreamDataTest {

    @Test
    @Timeout(2)
    void whenConfHasKeyAndValueGeneratorAndEnumGenerator_expectedDataFromKafkaYamlGenerated() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Kafka kafka = mapper.readValue(ClassLoader.getSystemResource("kafka.yml"), KafkaProperties.class).getKafka();
        Pair<Object, Object> result = StreamData.createStream(Arrays.asList(kafka.getTopics()), (t, p) -> {}, Duration.ofSeconds(1)).blockFirst();
        assertThat(result).isNotNull().isEqualTo(Pair.of(110, new TestObject(new TestNestedObject("string", 25, TestEnum.FIRST))));
    }
}