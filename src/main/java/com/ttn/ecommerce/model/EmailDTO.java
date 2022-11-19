package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class EmailDTO {
    @Email(message = "Enter a valid email.")
    private String email;
}
