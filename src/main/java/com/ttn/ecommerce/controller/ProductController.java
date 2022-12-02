package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.model.ProductDTO;
import com.ttn.ecommerce.model.ProductResponseDTO;
import com.ttn.ecommerce.model.ProductUpdateDTO;
import com.ttn.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="api/product")
public class ProductController{

    @Autowired
    ProductService productService;

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping(path ="/")
    public ResponseEntity<String> addProduct(Authentication authentication,@RequestBody ProductDTO productDTO){
        String response = productService.addProduct(authentication, productDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> fetchProduct(@PathVariable Long id, Authentication authentication){
        ProductResponseDTO productResponseDTO = productService.viewProduct(authentication,id);
        return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/")
    public ResponseEntity<List<ProductResponseDTO>> fetchAllProducts( Authentication authentication){
        List<ProductResponseDTO> response = productService.viewAllProducts(authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @DeleteMapping("/")
    public ResponseEntity<String> removeProduct( Authentication authentication,Long id){
        String response = productService.deleteProduct(authentication,id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping("/")
    public ResponseEntity<String> changeProduct(Authentication authentication, Long id, ProductUpdateDTO productUpdateDTO){
        String response = productService.updateProduct(id,authentication,productUpdateDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     ADMIN related APIs
     **/

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<ProductResponseDTO> viewAdminProduct(@PathVariable Long id){
        ProductResponseDTO response = productService.adminViewProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/")
    public ResponseEntity<List<ProductResponseDTO>> viewAdminProducts(){
        List<ProductResponseDTO> response = productService.adminViewAllProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/activate/{id}")
    public ResponseEntity<String> activateProduct(@PathVariable Long id){
        String response = productService.activateProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateProduct(@PathVariable Long id){
        String response = productService.deactivateProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
