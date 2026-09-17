package com.toolfix.repository;

import com.toolfix.domain.Product;
import com.toolfix.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShop(Shop shop);
    Optional<Product> findBySkuAndShop(String sku, Shop shop);
}
