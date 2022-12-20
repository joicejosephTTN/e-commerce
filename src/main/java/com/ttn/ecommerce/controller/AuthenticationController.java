package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.model.LoginDTO;
import com.ttn.ecommerce.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping(path="/logout")
    public ResponseEntity<String> userLogout(HttpServletRequest request){
        logger.info("AuthenticationController::userLogout request body: " + request.toString() );
        ResponseEntity<String> response = authenticationService.userSignOut(request);
        logger.info("AuthenticationController::userLogout response: " + response );
        return new ResponseEntity<>("User signed-out successfully.", HttpStatus.OK);

    }

}
