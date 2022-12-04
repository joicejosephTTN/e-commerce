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

    @PutMapping(path = "/activateAccount")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token){
        logger.info("NotificationTokenController::activateAccount request body: " + token);
        String response = notificationTokenService.activateUserAccount(token);
        logger.info("NotificationTokenController::activateAccount response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/resendActivation")
    public ResponseEntity<String> resendActivation(@Valid @RequestBody EmailDTO emailDTO){
        logger.info("NotificationTokenController::resendActivation request body: " + emailDTO);
        String response = notificationTokenService.resendActivationMail(emailDTO.getEmail());
        logger.info("NotificationTokenController::resendActivation response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/forgotPassword")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailDTO emailDTO){
        logger.info("NotificationTokenController::forgotPassword request body: " + emailDTO);
        String response = notificationTokenService.forgotPassword(emailDTO.getEmail());
        logger.info("NotificationTokenController::forgotPassword response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(path="/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @Valid @RequestBody PasswordDTO passwordDTO){
        logger.info("NotificationTokenController::resetPassword request body: " + token+ "," + passwordDTO);
        String response = notificationTokenService.resetPassword(token, passwordDTO.getPassword(), passwordDTO.getConfirmPassword());
        logger.info("NotificationTokenController::resetPassword response: " + response );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
