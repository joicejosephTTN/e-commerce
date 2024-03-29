package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Product;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path="api/product")
public class ProductController{

    @Autowired
    ProductService productService;

    /**
     SELLER related APIs
     **/
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
    public ResponseEntity<List<ProductResponseDTO>> fetchAllProducts(Authentication authentication){
        List<ProductResponseDTO> response = productService.viewAllProducts(authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeProduct( @PathVariable Long id, Authentication authentication ){
        String response = productService.deleteProduct(authentication,id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping("/{id}")
    public ResponseEntity<String> changeProduct(Authentication authentication, @PathVariable Long id, @RequestBody ProductUpdateDTO productUpdateDTO){
        String response = productService.updateProduct(id,authentication,productUpdateDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping(path ="/variation")
    public ResponseEntity<String> addProductVariation( @Valid @RequestBody VariationDTO variationDTO){
        String response = productService.addVariation(variationDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping(path ="/variation/{id}")
    public ResponseEntity<List<VariationResponseDTO>> fetchProductVariation(@PathVariable Long id,Authentication authentication){
        List<VariationResponseDTO> response = productService.viewProductVariation(id, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping(path ="/particularVariation/{id}")
    public ResponseEntity<VariationResponseDTO> fetchVariation(@PathVariable Long id,Authentication authentication){
        VariationResponseDTO response = productService.viewVariation(id, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping("/variation/{id}")
    public ResponseEntity<String> changeVariation(Authentication authentication, @PathVariable Long id, @RequestBody VariationUpdateDTO variationUpdateDTO){
        String response = productService.updateProductVariation(id,authentication,variationUpdateDTO);
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
    @PutMapping("/activate/{id}")
    public ResponseEntity<String> activateProduct(@PathVariable Long id){
        String response = productService.activateProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateProduct(@PathVariable Long id){
        String response = productService.deactivateProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     CUSTOMER related APIs
     **/

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customer/{id}")
    public ResponseEntity<CustomerProductResponseDTO > viewCustomerProduct(@PathVariable Long id){
        CustomerProductResponseDTO response = productService.customerViewProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customer/all/{id}")
    public ResponseEntity<List<CustomerProductResponseDTO>> viewAllCustomerProducts(@PathVariable Long id){
        List<CustomerProductResponseDTO> response = productService.customerViewAllProducts(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customer/similar/{id}")
    public ResponseEntity<List<ProductResponseDTO>> viewSimilarProducts(@PathVariable Long id){
        List<ProductResponseDTO> response = productService.viewSimilarProducts(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
