package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class SellerMetaResponseDTO {
    private Long metadataId;
    private String fieldName;
    private String possibleValues;
}
