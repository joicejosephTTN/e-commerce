package com.ttn.ecommerce.controller;

import com.ttn.ecommerce.entity.Category;
import com.ttn.ecommerce.entity.CategoryMetadataField;
import com.ttn.ecommerce.entity.CategoryMetadataFieldValue;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(path = "/api/category")
public class CategoryController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    CategoryService categoryService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<String> addCategory(@RequestBody CategoryDTO categoryDTO){
        Long id = categoryService.createCategory(categoryDTO);
        String response = messageSource.getMessage("api.response.addedSuccess",null, Locale.ENGLISH);
        return new ResponseEntity<>(response+"Category Id: " + id,HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/metadata")
    public ResponseEntity<String> addMetadata(@RequestBody MetadataDTO metadataDTO){
        Long id = categoryService.createMetadataField(metadataDTO);
        String response = messageSource.getMessage("api.response.addedSuccess",null, Locale.ENGLISH);
        return new ResponseEntity<>(response+"Category Id: " + id,HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/metadata")
    public ResponseEntity<Page<CategoryMetadataField>> viewMetadata(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                                    @RequestParam(required = false) String sortOrder){
        Page<CategoryMetadataField> fieldList = categoryService.viewAllMetadataFields(pageNo,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(fieldList,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<CategoryResponseDTO>> viewAllCategory(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                          @Pattern(regexp="DESC|ASC") @RequestParam(required = false) String sortOrder){
        List<CategoryResponseDTO> fieldList = categoryService.viewAllCategories(pageNo,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(fieldList,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> viewCategory(@PathVariable int id){
        CategoryResponseDTO category = categoryService.viewCategory(id);
        return new ResponseEntity<>(category,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/")
    public ResponseEntity<String> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO){
        String response = categoryService.updateCategoryName(categoryUpdateDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/metadata/values")
    public ResponseEntity<CategoryMetadataFieldValue> addMetaFieldValues(@RequestBody MetaFieldValueDTO metaFieldValueDTO){
        Long categoryId = metaFieldValueDTO.getCategoryId();
        Long metaFieldId = metaFieldValueDTO.getMetadataId();
        CategoryMetadataFieldValue response = categoryService.addMetaFieldValues(metaFieldValueDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
