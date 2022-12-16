package com.ttn.ecommerce.controller;


import com.ttn.ecommerce.model.CustomerResponseDTO;
import com.ttn.ecommerce.model.SellerResponseDTO;
import com.ttn.ecommerce.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping(path = "api/admin")
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminService adminService;


    @GetMapping(path="/customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                                     @RequestParam(required = false) String email){
        logger.info("AdminController::getAllCustomers request body: " +pageNo+", "+pageSize+", "+sortBy+", "+email );
        List<CustomerResponseDTO> customers = adminService.listAllCustomers(pageNo, pageSize, sortBy, email);
        logger.info("AdminController::getAllCustomers response: list of customers.");
        return new ResponseEntity<List<CustomerResponseDTO>>(customers,HttpStatus.OK);
    }


    @GetMapping(path="/sellers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<SellerResponseDTO>> getAllSellers(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                                 @RequestParam(defaultValue = "id") String sortBy,
                                                                 @RequestParam(required = false) String email){
        logger.info("AdminController::getAllSellers request body: " +pageNo+", "+pageSize+", "+sortBy+", "+email );
        List<SellerResponseDTO> sellers = adminService.listAllSellers(pageNo, pageSize, sortBy, email);
        logger.info("AdminController::getAllCustomers response: list of sellers.");
        return new ResponseEntity<>(sellers,HttpStatus.OK);
    }

    @PatchMapping(path="/activate")
    @Validated
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String>  enableUser(@RequestParam(value = "id") Long id){
        logger.info("AdminController::enableUser request body: "+ id);
        String response = adminService.activateUser(id);
        logger.info("AdminController::enableUser response : "+ response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping(path="/deactivate")
    @Validated
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String>  disableUser(@RequestParam("id") Long id){
        logger.info("AdminController::disableUser request body: "+ id);
        String response = adminService.deactivateUser(id);
        logger.info("AdminController::disableUser response : "+ response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
