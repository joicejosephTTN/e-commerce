package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class MetadataDTO {

    @NotEmpty(message = "Field name is a mandatory field")
    @Size(min = 2,max = 50 ,message = "Should be 2-50 characters")
    private String fieldName;
}
