package me.potato.redisperformance.service;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceV2 {
    private final ProductRepository repository;

    public Mono<Product> getProduct(int id) {
        return repository.findById(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return repository.findById(id)
                .flatMap(p -> productMono.doOnNext(product -> product.setId(id)))
                .flatMap(repository::save);
    }

    public Mono<Product> createProduct(Mono<Product> productMono) {
        return productMono.flatMap(repository::save);
    }
}
