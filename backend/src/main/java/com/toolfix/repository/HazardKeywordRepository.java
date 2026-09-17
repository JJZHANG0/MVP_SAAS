package com.toolfix.repository;

import com.toolfix.domain.HazardKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HazardKeywordRepository extends JpaRepository<HazardKeyword, Long> {
    List<HazardKeyword> findByActiveTrue();
}
