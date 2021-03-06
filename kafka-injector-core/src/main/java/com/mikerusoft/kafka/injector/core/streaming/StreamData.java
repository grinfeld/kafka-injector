package com.mikerusoft.kafka.injector.core.streaming;

import com.mikerusoft.kafka.injector.core.generate.model.NothingGenerator;
import com.mikerusoft.kafka.injector.core.properties.Generator;
import com.mikerusoft.kafka.injector.core.properties.Topic;
import com.mikerusoft.kafka.injector.core.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Slf4j
public class StreamData {

    private StreamData() {}

    public static <K, V> Flux<Pair<K,V>> createStream(List<Topic> topics, BiConsumer<String, List<Pair<K,V>>> consumer, Duration runningWindow) {
        Objects.requireNonNull(consumer);
        Flux<Pair<K,V>> stream = Flux.fromIterable(topics)
                .flatMap(StreamData::<K, V>createTopicGeneratorStreams)
                .doOnError(t -> log.error("Error on createTopicGeneratorStreams", t))
                .compose(pairFlux -> pairFlux
                        //.publishOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(topics.size()*3)))
                        .onBackpressureDrop()
                        .doOnNext(l -> consumer.accept(l.getLeft(), l.getRight()))
                        .doOnError(t -> log.error("Error on sending consumer", t))
                        .flatMap(t -> Flux.fromIterable(t.getRight()))
                );
        if (runningWindow != null) {
            stream = stream.take(runningWindow);
        }
        return stream;
    }

    public static <K, V> Future<?> subscribe(List<Topic> topics, BiConsumer<String, List<Pair<K,V>>> consumer, Duration runningWindow) {
        FutureTask<Boolean> finish = new FutureTask<>(() -> true);
        createStream(topics, consumer, runningWindow).doOnComplete(finish).subscribe();
        return finish;
    }

    private static <K, V> Flux<Pair<String, List<Pair<K, V>>>> createTopicGeneratorStreams(Topic topic) {
        int instancesPerTopic = countGenerators(topic);
        return Flux.just(topic)
            .map(StreamData::expandGenerators)
            .flatMap(Flux::fromStream)
            .flatMap(generatorPair ->
                Flux.interval(
                    Duration.ofMillis(generatorPair.getRight().getDelayAfter()), // delay before starting to emit elements
                    Duration.ofMillis(generatorPair.getRight().getInterval())
                ).takeWhile(i -> generatorPair.getRight().getTakeWhile() <= 0 || i < generatorPair.getRight().getTakeWhile())
                .map(i -> generatorPair)
                .map(g -> Pair.of(
                        // key generator
                        (K) g.getLeft().getGenerator().generate(g.getLeft().getFields()),
                        // value generator
                        (V) g.getRight().getGenerator().generate(g.getRight().getFields())
                ))
            )
            .buffer(Duration.ofSeconds(1)).map(l -> Pair.of(topic.getName(), l))
            .publishOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(instancesPerTopic)))
        ;
    }

    private static int countGenerators(Topic topic) {
        return Stream.of(topic.getValueGenerators()).map(Generator::getInstances).mapToInt(Long::intValue).sum();
    }

    private static Stream<Pair<Generator, Generator>> expandGenerators(Topic topic){
        return Stream.of(Pair.of(deNullKey(topic.getKeyGenerator()), topic.getValueGenerators()))
            .flatMap(
                pair -> Stream.of(pair.getRight()).map(g -> Pair.of(pair.getLeft(), g)).limit(pair.getRight().length)
            );
    }

    private static Generator deNullKey(Generator generator) {
        return generator == null ? Generator.buildFromType(NothingGenerator.class.getName()) : generator;
    }
}
