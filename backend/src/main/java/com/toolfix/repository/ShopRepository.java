package com.toolfix.repository;

import com.toolfix.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByShopifyDomain(String shopifyDomain);
    boolean existsByShopifyDomain(String shopifyDomain);
}
