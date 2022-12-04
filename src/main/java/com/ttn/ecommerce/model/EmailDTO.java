package com.ttn.ecommerce.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class EmailDTO {
    @NotEmpty(message = "Email is a mandatory field.")
    @Email(message = "Enter a valid email.")
    private String email;
}
