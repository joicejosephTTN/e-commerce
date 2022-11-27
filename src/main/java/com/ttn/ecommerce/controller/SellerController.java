package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.model.CustomerUpdateDTO;
import com.ttn.ecommerce.model.PasswordDTO;
import com.ttn.ecommerce.model.SellerUpdateDTO;
import com.ttn.ecommerce.model.SellerViewDTO;
import com.ttn.ecommerce.service.SellerService;
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

@RestController
@RequestMapping(path="/api/seller")
public class SellerController {
    Logger logger = LoggerFactory.getLogger(SellerController.class);
    @Autowired
    SellerService sellerService;

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping(path = "/profile")
    public SellerViewDTO viewProfile(Authentication authentication){
        logger.info("SellerController::viewProfile request body: " + authentication.toString());
        SellerViewDTO response = sellerService.fetchProfile(authentication.getName());
        logger.info("SellerController::viewProfile response: " + "[SellerViewDTO]");
        return response;
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping(path = "/update_profile")
    public ResponseEntity<String> updateProfile(Authentication authentication, @RequestPart("image") MultipartFile image,
                                                @Valid @RequestPart("details") SellerUpdateDTO sellerUpdateDTO){
        logger.info("SellerController::updateProfile request body: " + authentication.toString(),sellerUpdateDTO.toString()+ "[image]");
        String response = sellerService.updateProfile(authentication.getName(), sellerUpdateDTO, image);
        logger.info("SellerController::updateProfile response: " + response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping(path="/change_password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody PasswordDTO passwordDTO){
        logger.info("SellerController::changePassword request body: " + authentication.toString() + " [new password]");
        String response = sellerService.updatePassword(authentication.getName(), passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        logger.info("SellerController::changePassword response: " + response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
