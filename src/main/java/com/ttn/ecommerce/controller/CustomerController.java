package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path="/api/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping(path = "/profile")
    public CustomerViewDTO viewProfile(Authentication authentication){
        return customerService.fetchProfile(authentication.getName());
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping(path = "/address")
    public List<Address> viewAddresses(Authentication authentication){
        return customerService.fetchAddress(authentication.getName());
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/update_profile")
    public ResponseEntity<String> updateProfile(Authentication authentication, @Valid @RequestBody CustomerUpdateDTO customerUpdateDTO){
        String response = customerService.updateProfile(authentication.getName(), customerUpdateDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path="/change_password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody PasswordDTO passwordDTO){
        String response = customerService.updatePassword(authentication.getName(), passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping(path="/address/add")
    public ResponseEntity<String> newAddress(Authentication authentication, @Valid @RequestBody AddressDTO addressDTO){
        String response = customerService.addAddress(authentication.getName(),addressDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping(path="/address/remove")
    public ResponseEntity<String> removeAddress(Authentication authentication,@RequestParam("id") Long addressId){
        String response = customerService.deleteAddress(authentication.getName(),addressId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path="/address/update")
    public ResponseEntity<String> changeAddress(Authentication authentication,@RequestParam("id") Long addressId, @Valid @RequestBody AddressUpdateDTO addressDTO ){
        String response = customerService.updateAddress(authentication.getName(),addressId,addressDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
