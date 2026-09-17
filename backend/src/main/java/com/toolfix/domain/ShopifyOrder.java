package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shopify_orders")
@EqualsAndHashCode(callSuper = true)
public class ShopifyOrder extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;
    
    @Column(nullable = false, unique = true)
    private String shopifyOrderId;
    
    @Column(nullable = false)
    private String orderNumber;
    
    @Column(nullable = false)
    private String customerEmail;
    
    private String customerName;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String lineItems;
    
    @Column(nullable = false)
    private LocalDateTime orderCreatedAt;
    
    private String financialStatus;
    
    private String fulfillmentStatus;
}
