package com.mikerusoft.kafka.injector.examples.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mikerusoft.kafka.injector.core.KafkaInjectorApp;
import com.mikerusoft.kafka.injector.core.properties.Generator;
import com.mikerusoft.kafka.injector.core.properties.KafkaProperties;
import com.mikerusoft.kafka.injector.core.streaming.StreamData;
import com.mikerusoft.kafka.injector.examples.model.beans.wrappers.ExampleMessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class FieldGeneratorFactoryTest {


    @Test
    @Disabled
    void whenFileWithRecipientsAttachmentsOwner_expectedArchiveRawMessageWrapperGenerated() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        KafkaProperties kafkaProperties = mapper.readValue(ClassLoader.getSystemResource("test.yml"),
                KafkaProperties.class);
        Generator generator = kafkaProperties.getKafka().getTopics()[0].getValueGenerators()[0];
        Object generate = generator.getGenerator().generate(generator.getFields());
        assertThat(generate).isNotNull().isInstanceOf(ExampleMessageWrapper.class);
        ExampleMessageWrapper message = (ExampleMessageWrapper)generate;
        assertThat(message.getNetworkType()).isNotBlank();
        assertThat(message.getSourceType()).isNotBlank();
        assertNotNull(message.getBody());
        assertThat(message.getBody().getRecipients()).isNotNull().isNotEmpty();
        assertThat(message.getBody().getAttachment()).isNotNull().isNotEmpty();
        assertThat(message.getBody().getOwner()).isNotNull();
    }

    @Test
    @Disabled
    void testme() throws Exception {

        System.setProperty("kafkaInjectorConf", ClassLoader.getSystemResource("status_report.yml").getFile());
        KafkaInjectorApp.startFlow(Duration.ofHours(1)).get(1, TimeUnit.HOURS);

/*        StreamData.createStream(Arrays.asList(kafkaProperties.getKafka().getTopics()), (k, v) -> log.info("k: {}, v: {}", k, v.size()), Duration.ofMinutes(3))
            .blockLast();*/

      /*  Generator generator = kafkaProperties.getKafka().getTopics()[0].getValueGenerators()[0];
        Object generate = generator.getGenerator().generate(generator.getFields());
        System.out.println(generate);*/
    }
}
