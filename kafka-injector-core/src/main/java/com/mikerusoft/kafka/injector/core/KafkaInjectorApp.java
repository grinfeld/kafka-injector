package com.mikerusoft.kafka.injector.core;

import com.mikerusoft.kafka.injector.core.kafka.KafkaProducerConfiguration;
import com.mikerusoft.kafka.injector.core.properties.Topic;
import com.mikerusoft.kafka.injector.core.streaming.StreamData;
import com.mikerusoft.kafka.injector.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaInjectorApp {

    public static Future<?> startFlow(Duration duration) {
        List<Topic> topics = KafkaProducerConfiguration.getTopics();
        Runtime.getRuntime().addShutdownHook(new Thread(KafkaProducerConfiguration::closeAll));
        return StreamData.subscribe(topics, KafkaProducerConfiguration::insertIntoKafka, duration);
    }

    public static void main(String...args) throws Exception {
        String dimension = System.getProperty("timeUnit");
        TimeUnit timeUnit = TimeUnit.valueOf(dimension.toUpperCase());
        long time = Long.parseLong(System.getProperty("time"));
        Duration duration = Utils.ofDuration(time, timeUnit);
        try {
            startFlow(duration).get(time, timeUnit);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
