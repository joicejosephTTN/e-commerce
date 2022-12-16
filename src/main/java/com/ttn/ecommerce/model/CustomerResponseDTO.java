package com.ttn.ecommerce.model;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private long userId;
    private String fullName;
    private String email;
    private boolean isActive;
}
