package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Category;
import com.ttn.ecommerce.entity.Product;
import com.ttn.ecommerce.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameAndSellerAndCategoryAndBrand(String name, Seller seller,
                                                             Category category, String brand);

    List<Product> findByCategory(Category category);

    List<Product> findBySeller(Seller seller);
}