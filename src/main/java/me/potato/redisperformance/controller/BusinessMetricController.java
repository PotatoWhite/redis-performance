package me.potato.redisperformance.controller;


import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.service.BusinessMetricService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("product/metrics")
public class BusinessMetricController {
    private final BusinessMetricService metricService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<Integer, Double>> getMetrics() {
        return this.metricService.top3Products()
                .repeatWhen(l -> Flux.interval(Duration.ofSeconds(3)));
    }
}
