package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AddressUpdateDTO {

    @Pattern(regexp="(^[A-Za-z ]*$)",message = "Can only contain alphabets.")
    @Size(min = 2,max = 30)
    private String city;


    @Pattern(regexp="(^[A-Za-z ]*$)",message = "Can only contain alphabets.")
    @Size(min = 2,max = 30)
    private String state;


    @Pattern(regexp="(^[A-Za-z ]*$)",message = "Can only contain alphabets.")
    @Size(min = 2,max = 30)
    private String country;


    @Pattern(regexp="(^[A-Za-z0-9/., -]*$)",message = "Can only contain alphabets, numbers and '/'.")
    @Size(min = 2,max = 30)
    private String addressLine;

    @Pattern(regexp="(^[0-9]*$)",message = "Can only contain numbers.")
    @Size(min = 6,max = 6,message = "Zip code should only have 6 digits.")
    private String zipCode;

    private String label;

}
