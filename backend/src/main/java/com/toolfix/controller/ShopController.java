package com.toolfix.controller;

import com.toolfix.domain.Shop;
import com.toolfix.dto.ApiResponse;
import com.toolfix.repository.ShopRepository;
import com.toolfix.service.MockShopifyService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ShopController {
    
    private final ShopRepository shopRepository;
    private final MockShopifyService shopifyService;
    
    @GetMapping
    public ApiResponse<List<Shop>> getAllShops() {
        List<Shop> shops = shopRepository.findAll();
        return ApiResponse.success(shops);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Shop> getShop(@PathVariable Long id) {
        Shop shop = shopRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shop not found"));
        return ApiResponse.success(shop);
    }
    
    @PostMapping("/connect/init")
    public ApiResponse<ShopConnectInitResponse> initShopConnection(@RequestBody ShopConnectRequest request) {
        String state = UUID.randomUUID().toString();
        String authUrl = shopifyService.generateAuthUrl(request.getShopDomain(), state);
        
        return ApiResponse.success(new ShopConnectInitResponse(authUrl, state));
    }
    
    @PostMapping("/connect/callback")
    public ApiResponse<Shop> shopifyCallback(@RequestBody ShopifyCallbackRequest request) {
        Shop shop = shopifyService.connectShop(request.getShop(), request.getCode());
        return ApiResponse.success("Shop connected successfully", shop);
    }
    
    @PostMapping("/{id}/sync-orders")
    public ApiResponse<Void> syncOrders(@PathVariable Long id) {
        Shop shop = shopRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shop not found"));
        
        shopifyService.syncOrders(shop);
        return ApiResponse.success("Orders synced successfully", null);
    }
    
    @Data
    public static class ShopConnectRequest {
        private String shopDomain;
    }
    
    @Data
    @AllArgsConstructor
    public static class ShopConnectInitResponse {
        private String authUrl;
        private String state;
    }
    
    @Data
    public static class ShopifyCallbackRequest {
        private String shop;
        private String code;
        private String state;
    }
}
