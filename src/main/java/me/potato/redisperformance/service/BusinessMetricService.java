package me.potato.redisperformance.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BusinessMetricService {
    private final RedissonReactiveClient client;

    public Mono<Map<Integer, Double>> top3Products() {
        var                               formattedNow = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> set          = client.getScoredSortedSet("product:visit:" + formattedNow, IntegerCodec.INSTANCE);
        return set.entryRangeReversed(0, 2) // list of scored entry
                .map(listOfScored -> listOfScored.stream().collect(
                        Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (a, b) -> a,
                                LinkedHashMap::new
                        )
                ));
    }
}
