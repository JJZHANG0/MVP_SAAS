package com.toolfix.repository;

import com.toolfix.domain.Manual;
import com.toolfix.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Long> {
    Optional<Manual> findByProduct(Product product);
    Optional<Manual> findByProductId(Long productId);
}
