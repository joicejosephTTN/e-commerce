package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.model.CustomerResponseDTO;
import com.ttn.ecommerce.model.SellerResponseDTO;
import com.ttn.ecommerce.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path="/customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CustomerResponseDTO> getAllCustomers(){
        List<CustomerResponseDTO> customers = adminService.listAllCustomers();
        return customers;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path="/sellers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<SellerResponseDTO> getAllSellers(){
        List<SellerResponseDTO> sellers = adminService.listAllSellers();
        return sellers;
    }


    @PatchMapping(path="/activate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String>  enableUser(@RequestParam("id") Long id){
        String response = adminService.activateUser(id);
        return new ResponseEntity<String>(response,HttpStatus.OK);
    }

    @PatchMapping(path="/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String>  disableUser(@RequestParam("id") Long id){
        String response = adminService.deActivateUser(id);
        return new ResponseEntity<String>(response,HttpStatus.OK);
    }

}
