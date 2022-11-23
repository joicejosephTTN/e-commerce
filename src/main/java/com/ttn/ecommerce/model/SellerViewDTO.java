package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class SellerViewDTO {
    private long userId;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String companyContact;
    private String companyName;
    private String gst;
    private AddressDTO address;
}
