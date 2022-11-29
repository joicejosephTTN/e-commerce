package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Category;
import com.ttn.ecommerce.entity.CategoryMetadataField;
import com.ttn.ecommerce.entity.CategoryMetadataFieldKey;
import com.ttn.ecommerce.entity.CategoryMetadataFieldValue;
import com.ttn.ecommerce.exception.BadRequestException;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.repository.CategoryMetadataFieldRepository;
import com.ttn.ecommerce.repository.CategoryMetadataFieldValueRepository;
import com.ttn.ecommerce.repository.CategoryRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// add metadata -- takes a field name, should be unique. name and id will be saved in db
// view all metadata -- fetch all metadata records in the metadata table - pageable implement karna hai

@Service
public class CategoryService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    CategoryMetadataFieldValueRepository categoryMetadataFieldValueRepository;

    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public Long createCategory(CategoryDTO categoryDTO){

        Category category = new Category();

        if(categoryDTO.getParentId()!=null){
            Optional<Category> parentCategory = categoryRepository.findById(categoryDTO.getParentId());
            category.setParent(parentCategory.get());
        }
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category).getId();


    }

    public Long createMetadataField(MetadataDTO metadataDTO){

        String providedName = metadataDTO.getFieldName();

        CategoryMetadataField existingField = categoryMetadataFieldRepository.findByNameIgnoreCase(providedName);
        if(existingField!=null){
            throw new BadRequestException(messageSource.getMessage("api.error.fieldExists",null, Locale.ENGLISH));
        }
        CategoryMetadataField categoryMetadataField = new CategoryMetadataField();
        categoryMetadataField.setName(providedName);
        return categoryMetadataFieldRepository.save(categoryMetadataField).getId();
    }

    public Page<CategoryMetadataField> viewAllMetadataFields(Integer pageNo, Integer pageSize,
                                                             String sortBy, String sortOrder){
        if(sortOrder=="DESC"){
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            return categoryMetadataFieldRepository.findAll(paging);
        }
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return categoryMetadataFieldRepository.findAll(paging);

    }

    public CategoryResponseDTO viewCategory(int id){
        Category category = categoryRepository.findById((long) id).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(category.getId());
        categoryResponseDTO.setName(category.getName());
        categoryResponseDTO.setParent(category.getParent());
        categoryResponseDTO.setChildren(category.getChildren());
        return categoryResponseDTO;

    }

    public List<CategoryResponseDTO> viewAllCategories(Integer pageNo, Integer pageSize,
                                       String sortBy, String sortOrder){
            if (sortOrder == "DESC"){
                Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
                Page<Category> categoryPage = categoryRepository.findAll(paging);

                List<CategoryResponseDTO> requiredCategories = new ArrayList<>();

                CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();

                for (Category category: categoryPage){
                    categoryResponseDTO.setId(category.getId());
                    categoryResponseDTO.setName(category.getName());
                    categoryResponseDTO.setParent(category.getParent());
                    categoryResponseDTO.setChildren(category.getChildren());

                    requiredCategories.add(categoryResponseDTO);
                }
                return requiredCategories;
            }

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Category> categoryPage = categoryRepository.findAll(paging);
        List<CategoryResponseDTO> requiredCategories = new ArrayList<>();


        for (Category category: categoryPage){
            CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
            categoryResponseDTO.setId(category.getId());
            categoryResponseDTO.setName(category.getName());
            categoryResponseDTO.setParent(category.getParent());
            categoryResponseDTO.setChildren(category.getChildren());

            requiredCategories.add(categoryResponseDTO);
        }
        return requiredCategories;
    }

    public String updateCategoryName(CategoryUpdateDTO categoryUpdateDTO){
        Category category = categoryRepository.findById(categoryUpdateDTO.getId()).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        BeanUtils.copyProperties(categoryUpdateDTO,category, FilterProperties.getNullPropertyNames(category));
        categoryRepository.save(category);
        return messageSource.getMessage("api.response.updateSuccess",null,Locale.ENGLISH);
    }

    public CategoryMetadataFieldValue addMetaFieldValues(MetaFieldValueDTO metaFieldValueDTO){
        Long categoryId = metaFieldValueDTO.getCategoryId();
        Long metadataId = metaFieldValueDTO.getMetadataId();
        // check to see if provided id are valid
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        CategoryMetadataField metaField = categoryMetadataFieldRepository.findById(metadataId).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));


        CategoryMetadataFieldValue fieldValue = new CategoryMetadataFieldValue();

            fieldValue.setCategoryMetadataFieldKey(new CategoryMetadataFieldKey(category.getId(),metaField.getId()));
            fieldValue.setCategory(category);
            fieldValue.setCategoryMetadataField(metaField);
            String newValue = "";
            for(String value: metaFieldValueDTO.getValues()){
                newValue.concat("," + value);
            }
            fieldValue.setValue(newValue);
            categoryMetadataFieldValueRepository.save(fieldValue);

            return fieldValue;

        }



    }


