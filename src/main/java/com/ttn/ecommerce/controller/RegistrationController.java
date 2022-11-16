package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.model.CustomerDTO;
import com.ttn.ecommerce.model.SellerDTO;
import com.ttn.ecommerce.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    RegistrationService registrationService;

    //Test purpose
    @GetMapping(path="/users")
    public List<User> getAllUsers(){
        return registrationService.returnAllUsers();

    }

    @PostMapping(path = "/registration", headers = "user-role=SELLER")
    public ResponseEntity<Seller> registerUser(@RequestBody SellerDTO sellerDTO){
        Seller seller = registrationService.createSeller(sellerDTO);
        return new ResponseEntity<Seller>(seller, HttpStatus.CREATED);

    }

    @PostMapping(path = "/registration", headers = "user-role=CUSTOMER")
    public ResponseEntity<Customer> registerUser(@RequestBody CustomerDTO customerDTO){
        Customer customer = registrationService.createCustomer(customerDTO);
        return new ResponseEntity<Customer>(customer, HttpStatus.CREATED);
    }
}
