package com.mikerusoft.kafka.injector.examples;

import com.mikerusoft.kafka.injector.core.KafkaInjectorApp;
import com.mikerusoft.kafka.injector.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaInjectorAppIntegrationTest {

    @Test
    @Disabled
    void testInRaw() throws Exception {
        System.setProperty("kafkaInjectorConf", ClassLoader.getSystemResource("raw_in.yml").getFile());
        KafkaInjectorApp.startFlow(Utils.ofDuration(1, TimeUnit.MINUTES));
        KafkaInjectorApp.startFlow(Utils.ofDuration(1, TimeUnit.MINUTES)).get(1, TimeUnit.MINUTES);
    }

    @Test
    @Disabled
    void testStatusReport() throws Exception {
        System.setProperty("kafkaInjectorConf", ClassLoader.getSystemResource("status_report.yml").getFile());
        KafkaInjectorApp.startFlow(Utils.ofDuration(1, TimeUnit.MINUTES));
        KafkaInjectorApp.startFlow(Utils.ofDuration(1, TimeUnit.MINUTES)).get(1, TimeUnit.MINUTES);
    }

}
