package me.potato.redisperformance.service;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.service.util.CacheTemplate;
import me.potato.redisperformance.service.util.ProductCacheTemplate;
import me.potato.redisperformance.service.util.ProductLocalCacheTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceV2 {
    private final ProductCacheTemplate cacheTemplate;

    public Mono<Product> getProduct(int id) {
        return cacheTemplate.get(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return productMono
                .flatMap(product -> cacheTemplate.update(id, product));
    }

    public Mono<Void> deleteProduct(int id) {
        return cacheTemplate.delete(id);
    }
}
