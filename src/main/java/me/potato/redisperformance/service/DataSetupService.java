package me.potato.redisperformance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@RequiredArgsConstructor
@Service
public class DataSetupService implements CommandLineRunner {
    private final ProductRepository   repository;
    private final R2dbcEntityTemplate entityTemplate;

    @Value("classpath:schema.sql")
    private Resource resource;

    @Override
    public void run(String... args) throws Exception {
        var query = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        log.info(query);
        var insertMono = Flux.range(1, 1000)
                .map(i -> new Product(null, "Product " + i, ThreadLocalRandom.current().nextDouble(1, 1000)))
                .collectList()
                .flatMapMany(repository::saveAll)
                .then();

        this.entityTemplate.getDatabaseClient()
                .sql(query)
                .then()
                .then(insertMono)
                .doFinally(signalType -> log.info("Data setup completed {} ", signalType))
                .subscribe();
    }
}
