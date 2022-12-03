package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Address;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Data

public class SellerDTO extends UserDTO{

    @NotNull(message = "GST number is mandatory field.")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Enter a valid GST number")
    private String gst;

    @NotNull(message = "Phone number is mandatory field.")
    @Size(min=10,max=10,message = "Enter a valid phone number.")
    private String companyContact;

    @NotEmpty(message = "Company Name is mandatory.")
    @Size(max=30, message = "Enter a valid company name.")
    private String companyName;
    @Valid
    private AddressDTO address;
}
