package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Address;
import lombok.Data;

import javax.validation.Valid;

@Data
public class SellerProfileDTO {
    private long userId;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String companyContact;
    private String companyName;
    private String gst;
    @Valid
    private AddressDTO address;
}
