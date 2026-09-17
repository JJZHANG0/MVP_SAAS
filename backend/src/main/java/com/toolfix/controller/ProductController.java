package com.toolfix.controller;

import com.toolfix.domain.Product;
import com.toolfix.domain.Shop;
import com.toolfix.dto.ApiResponse;
import com.toolfix.repository.ProductRepository;
import com.toolfix.repository.ShopRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    
    @GetMapping
    public ApiResponse<List<Product>> getAllProducts(@RequestParam(required = false) Long shopId) {
        List<Product> products;
        if (shopId != null) {
            Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
            products = productRepository.findByShop(shop);
        } else {
            products = productRepository.findAll();
        }
        return ApiResponse.success(products);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return ApiResponse.success(product);
    }
    
    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody CreateProductRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
            .orElseThrow(() -> new RuntimeException("Shop not found"));
        
        Product product = new Product();
        product.setShop(shop);
        product.setSku(request.getSku());
        product.setProductName(request.getProductName());
        product.setModel(request.getModel());
        product.setBatteryVoltage(request.getBatteryVoltage());
        product.setBatteryType(request.getBatteryType());
        product.setRatedPower(request.getRatedPower());
        product.setRatedSpeed(request.getRatedSpeed());
        product.setCompatibleBatteryModels(request.getCompatibleBatteryModels());
        product.setKeyComponentCodes(request.getKeyComponentCodes());
        
        product = productRepository.save(product);
        return ApiResponse.success("Product created successfully", product);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getModel() != null) product.setModel(request.getModel());
        if (request.getBatteryVoltage() != null) product.setBatteryVoltage(request.getBatteryVoltage());
        if (request.getBatteryType() != null) product.setBatteryType(request.getBatteryType());
        if (request.getRatedPower() != null) product.setRatedPower(request.getRatedPower());
        if (request.getRatedSpeed() != null) product.setRatedSpeed(request.getRatedSpeed());
        if (request.getCompatibleBatteryModels() != null) product.setCompatibleBatteryModels(request.getCompatibleBatteryModels());
        if (request.getKeyComponentCodes() != null) product.setKeyComponentCodes(request.getKeyComponentCodes());
        
        product = productRepository.save(product);
        return ApiResponse.success("Product updated successfully", product);
    }
    
    @Data
    public static class CreateProductRequest {
        private Long shopId;
        private String sku;
        private String productName;
        private String model;
        private String batteryVoltage;
        private String batteryType;
        private String ratedPower;
        private String ratedSpeed;
        private String compatibleBatteryModels;
        private String keyComponentCodes;
    }
    
    @Data
    public static class UpdateProductRequest {
        private String productName;
        private String model;
        private String batteryVoltage;
        private String batteryType;
        private String ratedPower;
        private String ratedSpeed;
        private String compatibleBatteryModels;
        private String keyComponentCodes;
    }
}
