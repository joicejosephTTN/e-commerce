package com.ttn.ecommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @GetMapping("/")
    public String index(){
        return "Products";
    }

    @GetMapping("/admin/home")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminHome(){
        return "Admin home";
    }

    @GetMapping("/user/home")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userHome(){
        return "User home";
    }

    @GetMapping("/customer/home")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public String customerHome(){
        return "Customer home";
    }

    @GetMapping("/seller/home")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public String sellerHome(){
        return "Seller home";
    }
}
