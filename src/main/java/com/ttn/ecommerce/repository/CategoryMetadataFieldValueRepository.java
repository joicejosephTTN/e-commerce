package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.CategoryMetadataFieldKey;
import com.ttn.ecommerce.entity.CategoryMetadataFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldValueRepository extends JpaRepository<CategoryMetadataFieldValue, CategoryMetadataFieldKey> {
}