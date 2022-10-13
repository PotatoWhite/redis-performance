package me.potato.redisperformance.service;

import org.redisson.api.BatchOptions;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ProductVisitService {
    private final RedissonReactiveClient client;
    private final Sinks.Many<Integer>    sink;


    public ProductVisitService(RedissonReactiveClient client) {
        this.client = client;
        this.sink   = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    private void init() {
        this.sink.asFlux()
                .buffer(Duration.ofSeconds(3))// list (1,2,3,4,5,6,7,8,9,10)
                .map(list -> list.stream().collect( // 1:4, 5:1,
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        )
                ))
                .flatMap(this::updateBatch)
                .subscribe();
    }

    public void addVisit(int productId) {
        this.sink.tryEmitNext(productId);
    }

    private Mono<Void> updateBatch(Map<Integer, Long> map) {
        var batch        = this.client.createBatch(BatchOptions.defaults());
        var formattedNow = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        var set          = batch.getScoredSortedSet("product:visit:" + formattedNow, IntegerCodec.INSTANCE);

        return Flux.fromIterable(map.entrySet())
                .map(e -> set.addScore(e.getKey(), e.getValue()))
                .then(batch.execute())
                .then();
    }

}
