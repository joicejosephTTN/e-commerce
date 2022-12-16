package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class CustomerViewDTO {
    private long userId;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String contact;
    private byte[] image;

}
