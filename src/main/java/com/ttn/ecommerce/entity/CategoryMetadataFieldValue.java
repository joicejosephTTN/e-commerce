package com.ttn.ecommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ttn.ecommerce.utils.Auditable;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoryMetadataFieldValue extends Auditable<String> {

    @EmbeddedId
    private CategoryMetadataFieldKey categoryMetadataFieldKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("category_id")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("category_metadata_field_id")
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetadataField categoryMetadataField;

    private String value;

}
