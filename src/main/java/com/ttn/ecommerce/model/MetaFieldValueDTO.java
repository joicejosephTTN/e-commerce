package com.ttn.ecommerce.model;

import lombok.Data;

import java.util.List;


@Data
public class MetaFieldValueDTO {
    Long categoryId;
    Long metadataId;
    List<String> values;
}
