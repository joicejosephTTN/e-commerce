package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Category;
import com.ttn.ecommerce.entity.CategoryMetadataField;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryController {

    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    CategoryService categoryService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<String> addCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        logger.info("CategoryController::addCategory request:"+ categoryDTO);
        Long id = categoryService.createCategory(categoryDTO);
        String response = messageSource.getMessage("api.response.addedSuccess",null, Locale.ENGLISH);
        logger.info("CategoryController::addCategory response:"+ response +"ID: "+id);
        return new ResponseEntity<>(response+" Category Id: " + id,HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/metadata")
    public ResponseEntity<String> addMetadata(@Valid @RequestBody MetadataDTO metadataDTO){
        logger.info("CategoryController::addMetadata request:"+ metadataDTO);
        Long id = categoryService.createMetadataField(metadataDTO);
        String response = messageSource.getMessage("api.response.addedSuccess",null, Locale.ENGLISH);
        logger.info("CategoryController::addMetadata request:"+ response);
        return new ResponseEntity<>(response+" Category Id: " + id,HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/metadata")
    public ResponseEntity<Page<CategoryMetadataField>> viewMetadata(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @Pattern(regexp="DESC|ASC") @RequestParam(defaultValue = "ASC") String sortOrder){
        logger.info("CategoryController::viewMetadata request:"+ pageNo,pageSize,sortBy,sortOrder);
        if(sortOrder.equals("DESC")){
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            Page<CategoryMetadataField> fieldList = categoryService.viewAllMetadataFields(paging);
            logger.info("CategoryController::viewMetadata response :"+ "List size:"+ fieldList.getSize());
            return new ResponseEntity<>(fieldList,HttpStatus.OK);
        }
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<CategoryMetadataField> fieldList = categoryService.viewAllMetadataFields(paging);
        logger.info("CategoryController::viewMetadata response :"+ "List size:"+ fieldList.getSize());
        return new ResponseEntity<>(fieldList,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<CategoryResponseDTO>> viewAllCategory(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @Pattern(regexp="DESC|ASC") @RequestParam(defaultValue = "ASC") String sortOrder){
        logger.info("CategoryController::viewAllCategory request:"+ pageNo,pageSize,sortBy,sortOrder);
        if(sortOrder.equals("DESC")){
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            List<CategoryResponseDTO> fieldList = categoryService.viewAllCategories(paging);
            logger.info("CategoryController::viewAllCategory response :"+ "List size:"+ fieldList.size());
            return new ResponseEntity<>(fieldList,HttpStatus.OK);
        }
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        List<CategoryResponseDTO> fieldList = categoryService.viewAllCategories(paging);
        logger.info("CategoryController::viewAllCategory response :"+ "List size:"+ fieldList.size());
        return new ResponseEntity<>(fieldList,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> viewCategory(@PathVariable int id){
        logger.info("CategoryController::addCategory request:"+ "ID: "+id);
        CategoryResponseDTO category = categoryService.viewCategory(id);
        logger.info("CategoryController::addCategory response: ", category);
        return new ResponseEntity<>(category,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO){
        logger.info("CategoryController::updateCategory request: "+categoryUpdateDTO);
        String response = categoryService.updateCategoryName(categoryUpdateDTO);
        logger.info("CategoryController::updateCategory request: "+response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/metadata/values")
    public ResponseEntity<MetaFieldValueResponseDTO> addMetaFieldValues(@Valid @RequestBody MetaFieldValueDTO metaFieldValueDTO){
        logger.info("CategoryController::addMetaFieldValues request: "+ metaFieldValueDTO.toString());
        MetaFieldValueResponseDTO response = categoryService.addMetaValues(metaFieldValueDTO);
        logger.info("CategoryController::addMetaFieldValues response: ",response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/seller")
    public ResponseEntity< List<SellerCategoryResponseDTO> > viewSellerCategory(){
        List<SellerCategoryResponseDTO> responseList = categoryService.viewSellerCategory();
        logger.info("CategoryController::viewSellerCategory response[size]: "+responseList.size());
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping(value = {"/customer", "/customer/{id}"})
    public ResponseEntity<Set<Category>> viewCustomerCategory(@PathVariable("id") Optional<Integer> optionalId){
        logger.info("CategoryController::viewCustomerCategory request: "+ optionalId.get());
        Set<Category> responseList = categoryService.viewCustomerCategory(optionalId);
        logger.info("CategoryController::viewCustomerCategory response[size]: "+responseList.size());
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }


//    @PreAuthorize("hasAuthority('ADMIN')") // CHANGE TO CUSTOMER
////    @PreAuthorize("hasAuthority('CUSTOMER')")
//    @GetMapping("/filterCustomer/{id}")
//    public ResponseEntity<Set<Category>> filteredCustomerCategory(@PathVariable("id") Integer id){
//        Set<Category> responseList = categoryService.filterCustomerCategory(id);
//        return new ResponseEntity<>(responseList,HttpStatus.OK);
//    }





}
