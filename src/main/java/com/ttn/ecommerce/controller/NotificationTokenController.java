package com.ttn.ecommerce.controller;


import com.ttn.ecommerce.model.EmailDTO;
import com.ttn.ecommerce.model.PasswordDTO;
import com.ttn.ecommerce.service.NotificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequestMapping(path = "/api")
public class NotificationTokenController {

    @Autowired
    NotificationTokenService notificationTokenService;

    @PutMapping(path = "/activate_account")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token){
        String response = notificationTokenService.activateUserAccount(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/resend_activation")
    public ResponseEntity<String> resendActivation(@Valid @RequestBody EmailDTO emailDTO){
        String response = notificationTokenService.resendActivationMail(emailDTO.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/forgot_password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailDTO emailDTO){
        String response = notificationTokenService.forgotPassword(emailDTO.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(path="/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @Valid @RequestBody PasswordDTO passwordDTO){
        String response = notificationTokenService.resetPassword(token, passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
