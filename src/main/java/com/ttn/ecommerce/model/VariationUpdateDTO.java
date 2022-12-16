package com.ttn.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VariationUpdateDTO {
    private Map<String, Set<String>> metadata;
    //    private MultipartFile image;
    private int quantity;
    private float price;
    private boolean isActive;
}
