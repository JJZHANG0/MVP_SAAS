package com.toolfix.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.toolfix.domain.Shop;
import com.toolfix.domain.ShopifyOrder;
import com.toolfix.repository.ShopRepository;
import com.toolfix.repository.ShopifyOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockShopifyService {
    
    private final ShopRepository shopRepository;
    private final ShopifyOrderRepository shopifyOrderRepository;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    
    @Value("${toolfix.shopify.mock-enabled}")
    private boolean mockEnabled;
    
    public String generateAuthUrl(String shopDomain, String state) {
        if (mockEnabled) {
            return String.format("http://localhost:3000/shopify/mock-auth?shop=%s&state=%s", shopDomain, state);
        }
        
        return String.format("https://%s/admin/oauth/authorize?client_id=%s&scope=%s&redirect_uri=%s&state=%s",
            shopDomain, "CLIENT_ID", "read_orders,read_products", "REDIRECT_URI", state);
    }
    
    @Transactional
    public Shop connectShop(String shopDomain, String code) {
        String accessToken = mockEnabled ? "mock_token_" + System.currentTimeMillis() : "real_token";
        
        Shop shop = shopRepository.findByShopifyDomain(shopDomain)
            .orElse(new Shop());
        
        shop.setShopifyDomain(shopDomain);
        shop.setShopName(extractShopName(shopDomain));
        shop.setAccessToken(accessToken);
        shop.setPlatform("SHOPIFY");
        shop.setActive(true);
        shop.setOwnerEmail("owner@" + shopDomain);
        
        shop = shopRepository.save(shop);
        
        if (mockEnabled) {
            createMockOrders(shop);
        }
        
        log.info("Shop connected: {}", shopDomain);
        return shop;
    }
    
    private String extractShopName(String shopDomain) {
        if (shopDomain.contains(".")) {
            return shopDomain.substring(0, shopDomain.indexOf('.'));
        }
        return shopDomain;
    }
    
    private void createMockOrders(Shop shop) {
        List<ShopifyOrder> orders = new ArrayList<>();
        String[] products = {
            "Cordless Drill 20V:TD-20V-DRILL-001",
            "Impact Driver 18V:TD-18V-IMPACT-001",
            "Circular Saw 20V:TD-20V-SAW-001"
        };
        
        for (int i = 0; i < 10; i++) {
            ShopifyOrder order = new ShopifyOrder();
            order.setShop(shop);
            order.setShopifyOrderId("shopify_order_" + (1000 + i));
            order.setOrderNumber("#" + (2000 + i));
            order.setCustomerEmail("customer" + i + "@example.com");
            order.setCustomerName("Customer " + i);
            
            String[] selectedProduct = products[i % products.length].split(":");
            String lineItem = String.format("[{\"name\":\"%s\",\"sku\":\"%s\",\"quantity\":1}]",
                selectedProduct[0], selectedProduct[1]);
            order.setLineItems(lineItem);
            
            order.setOrderCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            order.setFinancialStatus("paid");
            order.setFulfillmentStatus("fulfilled");
            
            orders.add(order);
        }
        
        shopifyOrderRepository.saveAll(orders);
        log.info("Created {} mock orders for shop: {}", orders.size(), shop.getShopifyDomain());
    }
    
    public void syncOrders(Shop shop) {
        if (mockEnabled) {
            log.info("Mock order sync completed for shop: {}", shop.getShopifyDomain());
            return;
        }
        
        log.warn("Real Shopify order sync not implemented");
    }
}
