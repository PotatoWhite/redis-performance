package me.potato.redisperformance.service.util;

import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.repository.ProductRepository;
import org.redisson.api.*;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {
    private final ProductRepository                 repository;
    private       RLocalCachedMap<Integer, Product> map;

    public ProductLocalCacheTemplate(RedissonClient client, ProductRepository repository) {
        this.repository = repository;

        var localCachedMapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

        this.map = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class), localCachedMapOptions);
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return Mono.justOrEmpty(map.get(id));
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product entity) {
        return repository.findById(id)
                .doOnNext(product -> entity.setId(product.getId()))
                .flatMap(product -> repository.save(entity));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product entity) {
        return Mono.create(sink ->
                map.fastPutAsync(id, entity)
                        .thenAccept(b -> sink.success(entity))
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        }));
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.create(sink ->
                map.fastRemoveAsync(id)
                        .thenAccept(b -> sink.success())
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        }));
    }
}
