package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Data
public class VariationDTO {

    @NotNull(message = "metadataID is mandatory")
    @Min(value = 1,message = "Enter a valid ID")
    private Long productId;

    @NotNull(message = "Metadata is mandatory")
    @NotEmpty(message = "metadata cannot be empty.")
    private Map<String, String> metadata;
//    private MultipartFile image;

    @NotNull(message = "Quantity is mandatory")
    @Min(value = 1,message = "Enter a valid quantity")
    private Integer quantity;

    @NotNull(message = "Price is mandatory")
    @Min(value = 1,message = "Enter a valid price")
    private Float price;

}
