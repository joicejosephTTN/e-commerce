package com.ttn.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ttn.ecommerce.entity.Category;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String brand;

    private boolean isActive;

    private Category category;

}
