package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "knowledge_base")
@EqualsAndHashCode(callSuper = true)
public class KnowledgeBase extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String scenarioName;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptomDescription;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String rootCause;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String troubleshootingSteps;
    
    @Column(nullable = false)
    private String guidePageSlug;
    
    @Column(columnDefinition = "TEXT")
    private String keywords;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KnowledgeType type = KnowledgeType.PLATFORM_PRESET;
    
    private String relatedSku;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    public enum KnowledgeType {
        PLATFORM_PRESET,
        SKU_SPECIFIC
    }
}
