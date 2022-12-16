package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Product;
import com.ttn.ecommerce.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    List<ProductVariation> findByProduct(Product product);
}