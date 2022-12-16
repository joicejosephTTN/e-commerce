package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class MetadataResponseDTO {
    private Long metadataId;
    private String fieldName;
    private String possibleValues;
}
