package me.potato.redisperformance.controller;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.service.ProductServiceV3;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("product/v3")
public class ProductControllerV3 {
    private final ProductServiceV3 service;

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
