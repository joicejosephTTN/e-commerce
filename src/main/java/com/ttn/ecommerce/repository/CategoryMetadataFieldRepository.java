package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.CategoryMetadataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long> {
    CategoryMetadataField findByNameIgnoreCase(String name);
}