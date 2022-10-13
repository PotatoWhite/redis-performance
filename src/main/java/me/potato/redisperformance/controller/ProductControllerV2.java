package me.potato.redisperformance.controller;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.service.ProductServiceV1;
import me.potato.redisperformance.service.ProductServiceV2;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("product/v2")
public class ProductControllerV2 {
    private final ProductServiceV2 service;

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable int id) {
        return service.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable int id, @RequestBody Mono<Product> productMono) {
        return service.updateProduct(id, productMono);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteProduct(@PathVariable int id) {
        return service.deleteProduct(id);
    }
}
