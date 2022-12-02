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

    private Boolean isActive;

    private Boolean isCancellable;

    private Boolean isReturnable;

    private Category category;

}
