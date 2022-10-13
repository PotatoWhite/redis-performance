package me.potato.redisperformance.controller;

import lombok.RequiredArgsConstructor;
import me.potato.redisperformance.entity.Product;
import me.potato.redisperformance.service.ProductService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("product/v2")
public class ProductControllerV2 {
    private final ProductService service;

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable int id) {
        return service.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable int id, @RequestBody Mono<Product> productMono) {
        return service.updateProduct(id, productMono);
    }

    @PostMapping
    public Mono<Product> createProduct(@RequestBody Mono<Product> productMono) {
        return service.createProduct(productMono);
    }
}
