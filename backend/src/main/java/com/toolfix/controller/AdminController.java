package com.toolfix.controller;

import com.toolfix.domain.AIUsageLog;
import com.toolfix.domain.KnowledgeBase;
import com.toolfix.dto.ApiResponse;
import com.toolfix.repository.AIUsageLogRepository;
import com.toolfix.repository.KnowledgeBaseRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final AIUsageLogRepository aiUsageLogRepository;
    
    @GetMapping("/knowledge-base")
    public ApiResponse<List<KnowledgeBase>> getKnowledgeBase(
            @RequestParam(required = false) String type) {
        
        List<KnowledgeBase> knowledge;
        if (type != null) {
            KnowledgeBase.KnowledgeType typeEnum = KnowledgeBase.KnowledgeType.valueOf(type);
            knowledge = knowledgeBaseRepository.findByActiveTrueAndType(typeEnum);
        } else {
            knowledge = knowledgeBaseRepository.findByActiveTrue();
        }
        
        return ApiResponse.success(knowledge);
    }
    
    @GetMapping("/knowledge-base/{id}")
    public ApiResponse<KnowledgeBase> getKnowledge(@PathVariable Long id) {
        KnowledgeBase knowledge = knowledgeBaseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Knowledge not found"));
        return ApiResponse.success(knowledge);
    }
    
    @GetMapping("/ai-usage")
    public ApiResponse<Page<AIUsageLog>> getAIUsage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AIUsageLog> logs = aiUsageLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        return ApiResponse.success(logs);
    }
    
    @GetMapping("/ai-usage/stats")
    public ApiResponse<Map<String, Object>> getAIUsageStats(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        Long totalTokens = aiUsageLogRepository.sumTokensByCreatedAtAfter(startDate);
        BigDecimal totalCost = aiUsageLogRepository.sumCostByCreatedAtAfter(startDate);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("period", days + " days");
        stats.put("totalTokens", totalTokens != null ? totalTokens : 0);
        stats.put("totalCost", totalCost != null ? totalCost : BigDecimal.ZERO);
        stats.put("estimatedMonthlyCost", totalCost != null ? 
            totalCost.multiply(BigDecimal.valueOf(30.0 / days)) : BigDecimal.ZERO);
        
        stats.put("costEstimates", Map.of(
            "1000_sessions_per_month", "$12-18",
            "5000_sessions_per_month", "$60-90",
            "20000_sessions_per_month", "$240-360"
        ));
        
        return ApiResponse.success(stats);
    }
    
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("version", "1.0.0");
        return ApiResponse.success(health);
    }
}
