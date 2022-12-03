package com.ttn.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VariationResponseDTO {
    private  Long id;
    private Long productId;

    private Object metadata;

    //    private MultipartFile image;
    private long quantity;

    private double price;
}
