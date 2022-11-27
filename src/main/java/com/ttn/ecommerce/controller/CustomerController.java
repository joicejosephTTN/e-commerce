package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/customer")
public class CustomerController {
    Logger logger = LoggerFactory.getLogger(CustomerController.class);
    @Autowired
    CustomerService customerService;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping(path = "/profile")
    public CustomerViewDTO viewProfile(Authentication authentication) {
        logger.info("CustomerController::viewProfile request body: " + authentication.toString());
        CustomerViewDTO response = customerService.fetchProfile(authentication.getName());
        logger.info("CustomerController::viewProfile response: " + "[CustomerViewDTO]");
        return response;
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping(path = "/address")
    public List<Address> viewAddresses(Authentication authentication) {
        logger.info("CustomerController::viewAddresses request body: " + authentication.toString());
        List<Address> response = customerService.fetchAddress(authentication.getName());
        logger.info("CustomerController::viewAddresses response: " + response);
        return response;
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/update_profile")
    public ResponseEntity<String> updateProfile(Authentication authentication, @RequestPart("image") MultipartFile image,
                                                @Valid @RequestPart("details") CustomerUpdateDTO customerUpdateDTO) {
        logger.info("CustomerController::updateProfile request body: " + authentication.toString(),customerUpdateDTO.toString()+ "[image]");
        String response = customerService.updateProfile(authentication.getName(), customerUpdateDTO, image);
        logger.info("CustomerController::updateProfile response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/change_password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody PasswordDTO passwordDTO) {
        logger.info("CustomerController::changePassword request body: " + authentication.toString() + " [new password]");
        String response = customerService.updatePassword(authentication.getName(), passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        logger.info("CustomerController::changePassword response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping(path = "/address/add")
    public ResponseEntity<String> newAddress(Authentication authentication, @Valid @RequestBody AddressDTO addressDTO) {
        logger.info("CustomerController::newAddress request body: " + authentication.toString() + addressDTO.toString());
        String response = customerService.addAddress(authentication.getName(), addressDTO);
        logger.info("CustomerController::newAddress response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping(path = "/address/remove")
    public ResponseEntity<String> removeAddress(Authentication authentication, @RequestParam("id") Long addressId) {
        logger.info("CustomerController::removeAddress request body: " + authentication.toString() + "id- "+ addressId);
        String response = customerService.deleteAddress(authentication.getName(), addressId);
        logger.info("CustomerController::removeAddress response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/address/update")
    public ResponseEntity<String> changeAddress(Authentication authentication, @RequestParam("id") Long addressId, @Valid @RequestBody AddressUpdateDTO addressDTO) {
        logger.info("CustomerController::changeAddress request body: " + authentication.toString() + "id- "+ addressId + addressDTO.toString());
        String response = customerService.updateAddress(authentication.getName(), addressId, addressDTO);
        logger.info("CustomerController::changeAddress response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
