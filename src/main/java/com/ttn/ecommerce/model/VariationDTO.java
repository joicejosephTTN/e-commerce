package com.ttn.ecommerce.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class VariationDTO {
    private Long productId;
    private Map<String, Set<String>> metadata;
//    private MultipartFile image;
    private Integer quantity;
    private Float price;

}
