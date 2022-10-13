package me.potato.redisperformance.service;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.service.util.ProductLocalCacheTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceV3 {
    private final ProductLocalCacheTemplate cacheTemplate;
    private final ProductVisitService       visitService;

    public Mono<Product> getProduct(int id) {
        return cacheTemplate.get(id)
                .doFirst(() -> visitService.addVisit(id));
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return productMono
                .flatMap(product -> cacheTemplate.update(id, product));
    }

    public Mono<Void> deleteProduct(int id) {
        return cacheTemplate.delete(id);
    }
}
