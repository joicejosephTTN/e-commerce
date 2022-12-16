package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(path = "/api/customer")
public class CustomerController {
    Logger logger = LoggerFactory.getLogger(CustomerController.class);
    @Autowired
    CustomerService customerService;
    @Autowired
    MessageSource messageSource;

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
    public ResponseEntity<List<Address>> viewAddresses(Authentication authentication) {
        logger.info("CustomerController::viewAddresses request body: " + authentication.toString());
        List<Address> response = customerService.fetchAddress(authentication.getName());
        if(response.size() ==0){
            return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
        }
        logger.info("CustomerController::viewAddresses response: " + response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/profile")
    public ResponseEntity<String> updateProfile(Authentication authentication, @RequestPart(value = "image", required = false) MultipartFile image,
                                                @Valid @RequestPart(value = "details",required = false) CustomerUpdateDTO customerUpdateDTO) {
        if(image.isEmpty() && customerUpdateDTO==null){
            return new ResponseEntity<>(messageSource.getMessage("api.response.noUpdate",null, Locale.ENGLISH),HttpStatus.OK);
        }
        logger.info("CustomerController::updateProfile request body: " + authentication.toString()+ " "+customerUpdateDTO+ "[image]");
        String response = customerService.updateProfile(authentication.getName(), customerUpdateDTO, image);
        logger.info("CustomerController::updateProfile response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody PasswordDTO passwordDTO) {
        logger.info("CustomerController::changePassword request body: " + authentication.toString() + " [new password]");
        String response = customerService.updatePassword(authentication.getName(), passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        logger.info("CustomerController::changePassword response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping(path = "/address")
    public ResponseEntity<String> newAddress(Authentication authentication, @Valid @RequestBody AddressDTO addressDTO) {
        logger.info("CustomerController::newAddress request body: " + authentication.toString() + addressDTO.toString());
        String response = customerService.addAddress(authentication.getName(), addressDTO);
        logger.info("CustomerController::newAddress response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping(path = "/address")
    public ResponseEntity<String> removeAddress(Authentication authentication, @RequestParam("id") Long id) {
        logger.info("CustomerController::removeAddress request body: " + authentication.toString() + "id- "+ id);
        String response = customerService.deleteAddress(authentication.getName(), id);
        logger.info("CustomerController::removeAddress response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping(path = "/address")
    public ResponseEntity<String> changeAddress(Authentication authentication, @RequestParam("id") Long id, @Valid @RequestBody AddressUpdateDTO addressDTO) {

        logger.info("CustomerController::changeAddress request body: " + authentication.toString() + "id- "+ id + addressDTO);
        String response = customerService.updateAddress(authentication.getName(), id, addressDTO);
        logger.info("CustomerController::changeAddress response: " + response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
