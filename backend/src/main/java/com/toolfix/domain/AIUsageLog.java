package com.toolfix.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ai_usage_logs")
@EqualsAndHashCode(callSuper = true)
public class AIUsageLog extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    private DiagnosisSession session;
    
    @Column(nullable = false)
    private String operation;
    
    @Column(nullable = false)
    private String modelName;
    
    @Column(nullable = false)
    private Integer inputTokens;
    
    @Column(nullable = false)
    private Integer outputTokens;
    
    @Column(nullable = false)
    private Integer totalTokens;
    
    @Column(precision = 10, scale = 6)
    private BigDecimal estimatedCost;
    
    @Column(nullable = false)
    private Long responseTimeMs;
    
    @Column(columnDefinition = "TEXT")
    private String requestSummary;
    
    @Column(columnDefinition = "TEXT")
    private String responseSummary;
}
