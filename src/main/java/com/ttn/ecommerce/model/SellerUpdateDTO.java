package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SellerUpdateDTO {
    @NotEmpty(message = "First Name is mandatory.")
    @Size(min = 2, max = 30, message = "Must contain 2-30 characters.")
    @Pattern(regexp="(^[A-Za-z]*$)",message = "Invalid Input. " +
            "Name can only contain alphabets.")
    private String firstName;
    @NotEmpty(message = "Last Name is mandatory.")
    @Size(min = 2, max = 30, message = "Must contain 2-30 characters.")
    @Pattern(regexp="(^[A-Za-z]*$)",message = "Invalid Input. " +
            "Name can only contain alphabets. Must contain 2-30 characters.")
    private String lastName;
    @NotNull(message = "Phone number is mandatory field.")
    @Size(min=10,max=10,message = "Enter a valid phone number.")
    private String companyContact;
    @NotEmpty(message = "Company Name is mandatory.")
    @Size(max=30, message = "Enter a valid company name.")
    private String companyName;
    @NotNull(message = "GST number is mandatory field.")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Enter a valid GST number")
    private String gst;
    @Valid
    private AddressDTO address;
}
