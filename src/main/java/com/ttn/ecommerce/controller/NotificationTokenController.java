package com.ttn.ecommerce.controller;


import com.ttn.ecommerce.model.EmailDTO;
import com.ttn.ecommerce.model.PasswordDTO;
import com.ttn.ecommerce.service.NotificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequestMapping(path = "/api")
public class NotificationTokenController {
    Logger logger = LoggerFactory.getLogger(NotificationTokenController.class);

    @Autowired
    NotificationTokenService notificationTokenService;

    @PutMapping(path = "/activate_account")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token){
        logger.info("NotificationTokenController::activateAccount request body: " + token);
        String response = notificationTokenService.activateUserAccount(token);
        logger.info("NotificationTokenController::activateAccount response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/resend_activation")
    public ResponseEntity<String> resendActivation(@Valid @RequestBody EmailDTO emailDTO){
        logger.info("NotificationTokenController::resendActivation request body: " + emailDTO.toString());
        String response = notificationTokenService.resendActivationMail(emailDTO.getEmail());
        logger.info("NotificationTokenController::resendActivation response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/forgot_password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailDTO emailDTO){
        logger.info("NotificationTokenController::forgotPassword request body: " + emailDTO.toString());
        String response = notificationTokenService.forgotPassword(emailDTO.getEmail());
        logger.info("NotificationTokenController::forgotPassword response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(path="/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @Valid @RequestBody PasswordDTO passwordDTO){
        logger.info("NotificationTokenController::resetPassword request body: " + token+ "," + passwordDTO.toString());
        String response = notificationTokenService.resetPassword(token, passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        logger.info("NotificationTokenController::resetPassword response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
