package me.potato.redisperformance.service.util;

import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {
    private final ProductRepository repository;
    private RMapReactive<Integer, Product> map;

    public ProductCacheTemplate(RedissonReactiveClient client, ProductRepository repository) {
        this.repository = repository;
        this.map = client.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }
    @Override
    protected Mono<Product> getFromSource(Integer id){
        return this.repository.findById(id);
    }
    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return map.get(id);
    }
    @Override
    protected Mono<Product> updateSource(Integer id, Product entity) {
        return repository.findById(id)
                .doOnNext(product -> entity.setId(product.getId()))
                .flatMap(product -> repository.save(entity));
    }
    @Override
    protected Mono<Product> updateCache(Integer id, Product entity) {
        return this.map.fastPut(id, entity).thenReturn(entity);
    }
    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repository.deleteById(id);
    }
    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return map.fastRemove(id).then();
    }
}
