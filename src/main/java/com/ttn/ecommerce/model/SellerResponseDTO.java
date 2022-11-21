package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Address;
import lombok.Data;

@Data
public class SellerResponseDTO {
    private long userId;
    private String fullName;
    private String email;
    private boolean isActive;
    private String gst;
    private String companyContact;
    private String companyName;
    private Address address;
}
