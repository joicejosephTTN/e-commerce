package com.ttn.ecommerce.controller;
import com.ttn.ecommerce.BootstrapCommandLineRunner;
import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.model.CustomerDTO;
import com.ttn.ecommerce.model.SellerDTO;
import com.ttn.ecommerce.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    RegistrationService registrationService;


    @PostMapping(path = "/registration", headers = "user-role=SELLER")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SellerDTO sellerDTO){
        logger.info("RegistrationController::registerUser request body: " + sellerDTO.toString());

        String response = registrationService.createSeller(sellerDTO);
        logger.info("RegistrationController::registerUser response: " + response );
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping(path = "/registration", headers = "user-role=CUSTOMER")
    public ResponseEntity<String> registerUser(@Valid @RequestBody CustomerDTO customerDTO){
        logger.info("RegistrationController::registerUser request body: " + customerDTO.toString());
        String response = registrationService.createCustomer(customerDTO);
        logger.info("RegistrationController::registerUser response: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
