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

import java.util.*;

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

    public Long createCategory(CategoryDTO categoryDTO) {

        Category category = new Category();

        if (categoryDTO.getParentId() != null) {
            Optional<Category> parentCategory = categoryRepository.findById(categoryDTO.getParentId());
            category.setParent(parentCategory.get());
        }
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category).getId();


    }

    public Long createMetadataField(MetadataDTO metadataDTO) {

        String providedName = metadataDTO.getFieldName();

        CategoryMetadataField existingField = categoryMetadataFieldRepository.findByNameIgnoreCase(providedName);
        if (existingField != null) {
            throw new BadRequestException(messageSource.getMessage("api.error.fieldExists", null, Locale.ENGLISH));
        }
        CategoryMetadataField categoryMetadataField = new CategoryMetadataField();
        categoryMetadataField.setName(providedName);
        return categoryMetadataFieldRepository.save(categoryMetadataField).getId();
    }

    public Page<CategoryMetadataField> viewAllMetadataFields(Integer pageNo, Integer pageSize,
                                                             String sortBy, String sortOrder) {
        if (sortOrder == "DESC") {
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            return categoryMetadataFieldRepository.findAll(paging);
        }
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return categoryMetadataFieldRepository.findAll(paging);

    }

    public CategoryResponseDTO viewCategory(int id) {
        Category category = categoryRepository.findById((long) id).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(category.getId());
        categoryResponseDTO.setName(category.getName());
        categoryResponseDTO.setParent(category.getParent());


        Set<ChildCategoryDTO> childList = new HashSet<>();

        for(Category child: category.getChildren()){
            ChildCategoryDTO childCategoryDTO = new ChildCategoryDTO();
            childCategoryDTO.setId(child.getId());
            childCategoryDTO.setName(child.getName());
            childList.add(childCategoryDTO);

        }
        categoryResponseDTO.setChildren(childList);
        return categoryResponseDTO;

    }

    public List<CategoryResponseDTO> viewAllCategories(Integer pageNo, Integer pageSize,
                                                       String sortBy, String sortOrder) {
        if (sortOrder == "DESC") {
            Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            Page<Category> categoryPage = categoryRepository.findAll(paging);

            List<CategoryResponseDTO> requiredCategories = new ArrayList<>();

            CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();

            for (Category category : categoryPage) {
                categoryResponseDTO.setId(category.getId());
                categoryResponseDTO.setName(category.getName());
                categoryResponseDTO.setParent(category.getParent());

                Set<ChildCategoryDTO> childList = new HashSet<>();

                for(Category child: category.getChildren()){
                    ChildCategoryDTO childCategoryDTO = new ChildCategoryDTO();
                    childCategoryDTO.setId(child.getId());
                    childCategoryDTO.setName(child.getName());
                    childList.add(childCategoryDTO);

                }
                categoryResponseDTO.setChildren(childList);

                requiredCategories.add(categoryResponseDTO);
            }
            return requiredCategories;
        }

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Category> categoryPage = categoryRepository.findAll(paging);
        List<CategoryResponseDTO> requiredCategories = new ArrayList<>();


        for (Category category : categoryPage) {
            CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
            categoryResponseDTO.setId(category.getId());
            categoryResponseDTO.setName(category.getName());
            categoryResponseDTO.setParent(category.getParent());

            Set<ChildCategoryDTO> childList = new HashSet<>();

            for(Category child: category.getChildren()){
                ChildCategoryDTO childCategoryDTO = new ChildCategoryDTO();
                childCategoryDTO.setId(child.getId());
                childCategoryDTO.setName(child.getName());
                childList.add(childCategoryDTO);
            }
            categoryResponseDTO.setChildren(childList);

            requiredCategories.add(categoryResponseDTO);
        }
        return requiredCategories;
    }

    public String updateCategoryName(CategoryUpdateDTO categoryUpdateDTO) {
        Category category = categoryRepository.findById(categoryUpdateDTO.getId()).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        BeanUtils.copyProperties(categoryUpdateDTO, category, FilterProperties.getNullPropertyNames(category));
        categoryRepository.save(category);
        return messageSource.getMessage("api.response.updateSuccess", null, Locale.ENGLISH);
    }

    public MetaFieldValueResponseDTO addMetaFieldValues(MetaFieldValueDTO metaFieldValueDTO){

        Long categoryId = metaFieldValueDTO.getCategoryId();
        Long metadataId = metaFieldValueDTO.getMetadataId();

        // check to see if provided ids are valid
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        CategoryMetadataField metaField = categoryMetadataFieldRepository.findById(metadataId).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));


        // convert requestDTO to entity obj
        CategoryMetadataFieldValue fieldValue = new CategoryMetadataFieldValue();
        fieldValue.setCategoryMetadataFieldKey(new CategoryMetadataFieldKey(category.getId(), metaField.getId()));
        fieldValue.setCategory(category);
        fieldValue.setCategoryMetadataField(metaField);

        String newValues = "";


        // check to see if values are unique for category, metadataField combo
        CategoryMetadataFieldKey key = new CategoryMetadataFieldKey(categoryId,metadataId);
        String originalValues = categoryMetadataFieldValueRepository.findById(key).get().getValue();
        if(originalValues!=null){
            newValues = originalValues;
        }
        List<String> check = List.of(originalValues.split(","));



        for (String value : metaFieldValueDTO.getValues()){
            if(check.contains(value)){
                throw new BadRequestException(messageSource.getMessage("api.error.invalidFieldValue",null,Locale.ENGLISH));
            }
            // convert list of String values in request
            // to a comma separated String to be stored in database
            newValues = newValues.concat(value + ",");
        }
        fieldValue.setValue(newValues);

        // save field values to repo
        categoryMetadataFieldValueRepository.save(fieldValue);

        // create appropriate responseDTO
        MetaFieldValueResponseDTO metaFieldValueResponseDTO = new MetaFieldValueResponseDTO();
        metaFieldValueResponseDTO.setCategoryId(category.getId());
        metaFieldValueResponseDTO.setMetaFieldId(metaField.getId());
        metaFieldValueResponseDTO.setValues(fieldValue.getValue());

        return metaFieldValueResponseDTO;

    }

    public List<SellerCategoryResponseDTO> viewSellerCategory(){
        // obtain list of categories
        List<Category> categoryList = categoryRepository.findAll();

        List<SellerCategoryResponseDTO> resultList = new ArrayList<>();
        // filter out leaf nodes
        for(Category category: categoryList){
            if(category.getChildren().isEmpty()){
                // get its parental hierarchy till root node
                // use category to fetch all metadata fields and values related
                List<CategoryMetadataFieldValue> metadataList =
                        categoryMetadataFieldValueRepository.findByCategory(category);

                // convert to appropriate responseDTO

                SellerCategoryResponseDTO sellerResponse = new SellerCategoryResponseDTO();
                sellerResponse.setId(category.getId());
                sellerResponse.setName(category.getName());
                sellerResponse.setParent(category.getParent());
                // convert metadata and its values to appropriate responseDTO
                List<SellerMetaResponseDTO> metaList = new ArrayList<>();
                for (CategoryMetadataFieldValue metadata: metadataList){
                    SellerMetaResponseDTO sellerMetaResponseDTO = new SellerMetaResponseDTO();
                    sellerMetaResponseDTO.setMetadataId(metadata.getCategoryMetadataField().getId());
                    sellerMetaResponseDTO.setFieldName(metadata.getCategoryMetadataField().getName());
                    sellerMetaResponseDTO.setPossibleValues(metadata.getValue());
                    metaList.add(sellerMetaResponseDTO);
                }
                sellerResponse.setMetadata(metaList);
                resultList.add(sellerResponse);
            }
        }
        return resultList;
    }


}


