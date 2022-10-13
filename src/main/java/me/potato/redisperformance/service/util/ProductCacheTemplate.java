package me.potato.redisperformance.service.util;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {
    private final ProductRepository repository;
    private RMapReactive<Integer, Product> map;

    @Override
    protected Mono<Product> getFromSource(Integer integer) {
        return null;
    }
    @Override
    protected Mono<Product> getFromCache(Integer integer) {
        return null;
    }
    @Override
    protected Mono<Product> updateSource(Integer integer, Product entityMono) {
        return null;
    }
    @Override
    protected Mono<Product> updateCache(Integer integer, Product entityMono) {
        return null;
    }
    @Override
    protected Mono<Void> deleteFromSource(Integer integer) {
        return null;
    }
    @Override
    protected Mono<Void> deleteFromCache(Integer integer) {
        return null;
    }
}
