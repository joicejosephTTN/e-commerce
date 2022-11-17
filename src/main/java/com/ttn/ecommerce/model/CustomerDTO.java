package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Address;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerDTO extends UserDTO{
    @NotEmpty(message = "Phone number is a mandatory field.")
    @Pattern(regexp = "^\\d{10}$", message = "Enter a valid ten-digit phone number.")
    private String contact;

    private List<AddressDTO> address = new ArrayList<>();
}
