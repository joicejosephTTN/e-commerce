package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class MetaFieldValueResponseDTO {
    private Long categoryId;
    private Long metaFieldId;
    private String values;
}
