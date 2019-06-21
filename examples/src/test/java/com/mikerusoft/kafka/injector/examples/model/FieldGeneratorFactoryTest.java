package com.mikerusoft.kafka.injector.examples.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mikerusoft.kafka.injector.core.properties.Generator;
import com.mikerusoft.kafka.injector.core.properties.KafkaProperties;
import com.mikerusoft.kafka.injector.examples.model.beans.wrappers.StamMessageWrapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FieldGeneratorFactoryTest {


    @Test
    @Disabled
    void whenFileWithRecipientsAttachmentsOwner_expectedArchiveRawMessageWrapperGenerated() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        KafkaProperties kafkaProperties = mapper.readValue(ClassLoader.getSystemResource("test.yml"),
                KafkaProperties.class);
        Generator generator = kafkaProperties.getKafka().getTopics()[0].getGenerators()[0];
        Object generate = generator.getGenerator().generate(generator.getFields());
        assertThat(generate).isNotNull().isInstanceOf(StamMessageWrapper.class);
        StamMessageWrapper message = (StamMessageWrapper)generate;
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
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        KafkaProperties kafkaProperties = mapper.readValue(ClassLoader.getSystemResource("status_report.yml"),
                KafkaProperties.class);
        Generator generator = kafkaProperties.getKafka().getTopics()[0].getGenerators()[0];
        Object generate = generator.getGenerator().generate(generator.getFields());
        System.out.println(generate);
    }
}
