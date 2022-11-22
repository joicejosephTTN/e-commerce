package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.model.SellerProfileDTO;
import com.ttn.ecommerce.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/seller")
public class SellerController {
    @Autowired
    SellerService sellerService;

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping(path = "/profile")
    public SellerProfileDTO viewProfile(Authentication authentication){
        return sellerService.fetchProfile(authentication.getName());
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping(path = "/update_profile")
    public ResponseEntity<String> updateProfile(Authentication authentication, @RequestBody SellerProfileDTO sellerProfileDTO){
        String response = sellerService.updateProfile(authentication.getName(), sellerProfileDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
