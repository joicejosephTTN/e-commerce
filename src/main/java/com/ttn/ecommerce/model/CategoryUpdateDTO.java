package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CategoryUpdateDTO {

    @NotEmpty(message = "Name is a mandatory field.")
    private String name;

    @NotNull(message = "Category ID is mandatory")
    @Min(value = 1,message = "Enter a valid ID")
    private Long id;
}
