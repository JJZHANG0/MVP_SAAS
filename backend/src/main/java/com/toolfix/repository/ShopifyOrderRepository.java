package com.toolfix.repository;

import com.toolfix.domain.Shop;
import com.toolfix.domain.ShopifyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopifyOrderRepository extends JpaRepository<ShopifyOrder, Long> {
    Optional<ShopifyOrder> findByShopifyOrderId(String shopifyOrderId);
    List<ShopifyOrder> findByShopOrderByCreatedAtDesc(Shop shop);
    boolean existsByShopifyOrderId(String shopifyOrderId);
}
