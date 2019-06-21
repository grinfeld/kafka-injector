package com.mikerusoft.kafka.injector.core;

import com.mikerusoft.kafka.injector.core.kafka.KafkaProducerConfiguration;
import com.mikerusoft.kafka.injector.core.properties.Topic;
import com.mikerusoft.kafka.injector.core.streaming.StreamData;
import com.mikerusoft.kafka.injector.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaInjectorApp {

    public static Disposable startFlow(Duration duration) {
        List<Topic> topics = KafkaProducerConfiguration.getTopics();
        Disposable subscriber = StreamData.subscribe(topics, KafkaProducerConfiguration::insertIntoKafka, duration);
        Runtime.getRuntime().addShutdownHook(new Thread(subscriber::dispose));
        return subscriber;
    }

    public static void main(String...args) throws Exception {
        String dimension = System.getProperty("timeUnit");
        TimeUnit timeUnit = TimeUnit.valueOf(dimension.toUpperCase());
        long time = Long.parseLong(System.getProperty("time"));
        startFlow(Utils.ofDuration(time, timeUnit));
        waitUntilStopFor(time, timeUnit);
    }

    public static void waitUntilStopFor(long time, TimeUnit unit) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try { Thread.sleep(unit.toMillis(time)); } catch (Exception ignore) {}
                log.debug("End");
                System.exit(0);
            }
        });
        executorService.awaitTermination(time, unit);
    }

}
