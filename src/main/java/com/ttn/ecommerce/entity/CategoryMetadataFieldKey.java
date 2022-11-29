package com.ttn.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;


// Composite Key
@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CategoryMetadataFieldKey implements Serializable {
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_metadata_field_id")
    private Long categoryMetadataFieldId;
}
