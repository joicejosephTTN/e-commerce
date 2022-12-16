package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProductResponseDTO {
    private Long id;

    private String name;

    private String description;

    private String brand;

    private Boolean isActive;

    private Boolean isCancellable;

    private Boolean isReturnable;

    private Category category;

    private List<VariationResponseDTO> variations;
}
