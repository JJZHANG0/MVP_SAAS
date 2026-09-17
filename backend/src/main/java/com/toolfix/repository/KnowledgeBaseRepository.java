package com.toolfix.repository;

import com.toolfix.domain.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    
    List<KnowledgeBase> findByActiveTrue();
    
    List<KnowledgeBase> findByActiveTrueAndType(KnowledgeBase.KnowledgeType type);
    
    @Query("SELECT k FROM KnowledgeBase k WHERE k.active = true " +
           "AND (k.type = 'PLATFORM_PRESET' OR k.relatedSku = :sku)")
    List<KnowledgeBase> findActiveKnowledgeForSku(@Param("sku") String sku);
}
