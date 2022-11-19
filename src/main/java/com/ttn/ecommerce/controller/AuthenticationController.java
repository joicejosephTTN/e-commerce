package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.model.LoginDTO;
import com.ttn.ecommerce.service.AuthenticationService;
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

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping(path = "/login")
    public ResponseEntity<String> userLogin(@RequestBody LoginDTO loginDTO){
        authenticationService.userSignIn(loginDTO.getEmail(), loginDTO.getPassword());
        return new ResponseEntity<>("User signed-in successfully.", HttpStatus.OK);
    }

    @PostMapping(path="/logout")
    public ResponseEntity<String> userLogout(HttpServletRequest request){
        ResponseEntity<String> response = authenticationService.userSignOut(request);
        return response;

    }


}
