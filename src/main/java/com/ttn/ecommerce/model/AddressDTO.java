package com.ttn.ecommerce.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class AddressDTO {

    @NotEmpty(message = "This is a mandatory field.")
    @Pattern(regexp="(^[A-Za-z ]*$)",message = "Can only contain alphabets.")
    @Size(min = 2,max = 30)
    private String city;

    @NotEmpty(message = "This is a mandatory field.")
    @Pattern(regexp="(^[A-Za-z ]*$)",message = "Can only contain alphabets.")
    @Size(min = 2,max = 30)
    private String state;

    @NotEmpty(message = "This is a mandatory field.")
    @Pattern(regexp="(^[A-Za-z ]*$)",message = "Can only contain alphabets.")
    @Size(min = 2,max = 30)
    private String country;

    @NotEmpty(message = "This is a mandatory field.")
    @Pattern(regexp="(^[A-Za-z0-9/., -]*$)",message = "Can only contain alphabets, numbers and '/'.")
    @Size(min = 2,max = 30)
    private String addressLine;

    @NotEmpty(message = "This is a mandatory field.")
    @Pattern(regexp="(^[0-9]*$)",message = "Can only contain numbers.")
    @Size(min = 6,max = 6,message = "Zip code should only have 6 digits.")
    private String pinCode;

    private String label;

}