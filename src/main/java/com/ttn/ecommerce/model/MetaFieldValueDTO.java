package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class MetaFieldValueDTO {

    @NotNull(message = "Category ID is mandatory")
    @Min(value = 1,message = "Enter a valid ID")
    Long categoryId;

    @NotNull(message = "metadataID is mandatory")
    @Min(value = 1,message = "Enter a valid ID")
    Long metadataId;

    @NotEmpty(message = "values list cannot be empty.")
    List<String> values;
}
