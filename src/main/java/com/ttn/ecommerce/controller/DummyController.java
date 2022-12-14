package com.ttn.ecommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// DUMMY CONTROLLER to test security implementation

@RestController
public class DummyController {
    @GetMapping("/")
    public String index(){
        return "Products";
    }

    @GetMapping("/admin/home")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminHome(){
        return "Admin home";
    }

    @GetMapping("/user/home")
    @PreAuthorize("hasAuthority('USER')")
    public String userHome(){
        return "User home";
    }

    @GetMapping("/customer/home")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public String customerHome(){
        return "Customer home";
    }

    @GetMapping("/seller/home")
    @PreAuthorize("hasAuthority('SELLER')")
    public String sellerHome(){
        return "Seller home";
    }
}
