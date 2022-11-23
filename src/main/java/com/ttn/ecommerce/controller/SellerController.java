package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.model.PasswordDTO;
import com.ttn.ecommerce.model.SellerUpdateDTO;
import com.ttn.ecommerce.model.SellerViewDTO;
import com.ttn.ecommerce.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path="/api/seller")
public class SellerController {
    @Autowired
    SellerService sellerService;

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping(path = "/profile")
    public SellerViewDTO viewProfile(Authentication authentication){
        return sellerService.fetchProfile(authentication.getName());
    }

//    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping(path = "/update_profile")
    public ResponseEntity<String> updateProfile(Authentication authentication,@Valid @RequestBody SellerUpdateDTO sellerUpdateDTO){
        String response = sellerService.updateProfile(authentication.getName(), sellerUpdateDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping(path="/change_password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody PasswordDTO passwordDTO){
        String response = sellerService.updatePassword(authentication.getName(), passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
