package com.toolfix.repository;

import com.toolfix.domain.AIUsageLog;
import com.toolfix.domain.DiagnosisSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AIUsageLogRepository extends JpaRepository<AIUsageLog, Long> {
    
    List<AIUsageLog> findBySessionOrderByCreatedAtAsc(DiagnosisSession session);
    
    Page<AIUsageLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT SUM(a.totalTokens) FROM AIUsageLog a WHERE a.createdAt >= :startDate")
    Long sumTokensByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(a.estimatedCost) FROM AIUsageLog a WHERE a.createdAt >= :startDate")
    BigDecimal sumCostByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);
}
