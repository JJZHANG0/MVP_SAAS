package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "products")
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String sku;
    
    @Column(nullable = false)
    private String productName;
    
    private String model;
    
    private String batteryVoltage;
    
    private String batteryType;
    
    private String ratedPower;
    
    private String ratedSpeed;
    
    private String compatibleBatteryModels;
    
    private String keyComponentCodes;
    
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;
    
    @Column(nullable = false)
    private Boolean hasManual = false;
}
