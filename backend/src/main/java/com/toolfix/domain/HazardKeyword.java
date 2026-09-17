package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "hazard_keywords")
@EqualsAndHashCode(callSuper = true)
public class HazardKeyword extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String keyword;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HazardLevel level = HazardLevel.HIGH;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String safetyResponse;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    public enum HazardLevel {
        CRITICAL,
        HIGH,
        MEDIUM
    }
}
