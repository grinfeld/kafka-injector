package com.mikerusoft.kafka.injector.core.streaming;

import com.mikerusoft.kafka.injector.core.properties.Generator;
import com.mikerusoft.kafka.injector.core.properties.Topic;
import com.mikerusoft.kafka.injector.core.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class StreamData {

    private StreamData() {}

    public static <T> Disposable subscribe(List<Topic> topics, BiConsumer<String, List<T>> consumer, Duration runningWindow) {
        Objects.requireNonNull(consumer);
        Flux<T> stream = Flux.fromIterable(topics)
            .flatMap(StreamData::createTopicGeneratorStreams)
            .doOnError(t -> log.error("Error on createTopicGeneratorStreams", t))
            .compose(pairFlux -> pairFlux
                .doOnNext(l -> consumer.accept(l.getLeft(), (List<T>) l.getRight()))
                .doOnError(t -> log.error("Error on sending consumer", t))
                .flatMap(t -> Flux.fromIterable((List<T>) t.getRight()))
            );
        if (runningWindow != null) {
            stream = stream.take(runningWindow);
        }
        return stream.subscribe();
    }

    private static <T> Flux<Pair<String, List<T>>> createTopicGeneratorStreams(Topic topic) {
        return Flux.just(topic)
            .map(StreamData::expandGenerators)
            .flatMap(Flux::fromStream)
            .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(countGenerators(topic))))
            .flatMap(generator ->
                Flux.interval(
                        Duration.ofMillis(generator.getDelayAfter()), // delay before starting to emit elements
                        Duration.ofMillis(generator.getInterval())
                )
                .takeWhile(i -> generator.getTakeWhile() <= 0 || i < generator.getTakeWhile())
                .map(i -> generator)
                .map(g -> (T) g.getGenerator().generate(g.getFields()))
                //.doOnNext(t -> { log.info("generated " + String.valueOf(t)); })
            ).buffer(Duration.ofSeconds(1)).map(l -> Pair.of(topic.getName(), l))
        ;
    }

    private static int countGenerators(Topic topic) {
        return Stream.of(topic.getGenerators()).map(Generator::getInstances).mapToInt(Long::intValue).sum();
    }

    private static Stream<Generator> expandGenerators(Topic topic){
        return Stream.of(topic.getGenerators()).flatMap(g -> Stream.generate(() -> g).limit(g.getInstances()));
    }
}
