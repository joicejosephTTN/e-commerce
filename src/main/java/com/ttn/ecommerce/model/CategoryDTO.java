package com.ttn.ecommerce.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    @NotEmpty(message = "Name is a mandatory field.")
    private String name;

    @Min(value = 0,message = "Enter a valid ID")
    private Long parentId;
}
