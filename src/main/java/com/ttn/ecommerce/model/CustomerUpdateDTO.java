package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CustomerUpdateDTO {

    @Size(min = 2, max = 30, message = "Must contain 2-30 characters.")
    @Pattern(regexp="(^[A-Za-z]*$)",message = "Invalid Input. " +
            "Name can only contain alphabets.")
    private String firstName;

    @Size(min = 2, max = 30, message = "Must contain 2-30 characters.")
    @Pattern(regexp="(^[A-Za-z]*$)",message = "Invalid Input. " +
            "Name can only contain alphabets. Must contain 2-30 characters.")
    private String lastName;

    @Size(min=10,max=10,message = "Enter a valid phone number.")
    private String contact;
}
