package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private Integer categoryId;
    private String brand;
    private String description;

}
