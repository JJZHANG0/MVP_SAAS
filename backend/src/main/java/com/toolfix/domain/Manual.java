package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "manuals")
@EqualsAndHashCode(callSuper = true)
public class Manual extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @Column(nullable = false)
    private String storedFileName;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManualStatus status = ManualStatus.UNCONFIRMED;
    
    @Column(columnDefinition = "TEXT")
    private String extractedProductName;
    
    @Column(columnDefinition = "TEXT")
    private String extractedModel;
    
    @Column(columnDefinition = "TEXT")
    private String extractedBatteryInfo;
    
    @Column(columnDefinition = "TEXT")
    private String extractedPowerInfo;
    
    @Column(columnDefinition = "TEXT")
    private String extractedCompatibleBatteries;
    
    @Column(columnDefinition = "TEXT")
    private String extractedComponentCodes;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String extractedSafetyWarnings;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String extractedWarrantyTerms;
    
    @Column(columnDefinition = "TEXT")
    private String vectorIds;
    
    private String parseJobId;
    
    @Column(columnDefinition = "TEXT")
    private String parseErrorMessage;
    
    public enum ManualStatus {
        UNCONFIRMED,
        LOCKED
    }
}
