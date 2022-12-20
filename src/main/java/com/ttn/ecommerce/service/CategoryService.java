package com.ttn.ecommerce.service;

import com.ttn.ecommerce.controller.CategoryController;
import com.ttn.ecommerce.entity.*;
import com.ttn.ecommerce.exception.BadRequestException;
import com.ttn.ecommerce.model.*;
import com.ttn.ecommerce.repository.CategoryMetadataFieldRepository;
import com.ttn.ecommerce.repository.CategoryMetadataFieldValueRepository;
import com.ttn.ecommerce.repository.CategoryRepository;
import com.ttn.ecommerce.repository.ProductRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CategoryService {

    Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    MessageSource messageSource;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryMetadataFieldValueRepository categoryMetadataFieldValueRepository;

    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public Long createCategory(CategoryDTO categoryDTO) {
        logger.info("CategoryService::createCategory execution started");
        Category category = new Category();

        logger.debug("CategoryService::createCategory creating new category");
        if (categoryDTO.getParentId() != null) {
            // check to see if passed parentID is valid
            Optional<Category> parentCategory = categoryRepository.findById(categoryDTO.getParentId());
            if(parentCategory.isEmpty()){
                logger.error("CategoryService::createCategory an exception occurred while creating the category");
                throw new BadRequestException(messageSource.getMessage("api.error.invalidParentId",null,Locale.ENGLISH));
            }
            // check if parentId has any product associated with it
            List<Product> product = productRepository.findByCategory(parentCategory.get());
            if (product.size()>0){
                logger.error("CategoryService::createCategory an exception occurred while creating the category");
                throw new BadRequestException(messageSource.getMessage("api.error.productAssociatedCategory",null,Locale.ENGLISH));
            }
            category.setParent(parentCategory.get());
        }

        category.setName(categoryDTO.getName());
        logger.info("CategoryService::createCategory execution ended.");
        return categoryRepository.save(category).getId();

    }

    public Long createMetadataField(MetadataDTO metadataDTO) {
        logger.info("CategoryService::createMetadataField execution started");
        String providedName = metadataDTO.getFieldName();
        logger.debug("CategoryService::createMetadataField adding a new field");

        CategoryMetadataField existingField = categoryMetadataFieldRepository.findByNameIgnoreCase(providedName);
        if (existingField != null) {
            logger.error("CategoryService::createMetadataField an exception occurred while adding");
            throw new BadRequestException(messageSource.getMessage("api.error.fieldExists", null, Locale.ENGLISH));
        }
        CategoryMetadataField categoryMetadataField = new CategoryMetadataField();
        categoryMetadataField.setName(providedName);
        logger.info("CategoryService::createMetadataField execution ended");
        return categoryMetadataFieldRepository.save(categoryMetadataField).getId();
    }

    public Page<CategoryMetadataField> viewAllMetadataFields(Pageable paging) {
        logger.info("CategoryService::viewAllMetadataFields execution started.");
        logger.info("CategoryService::viewAllMetadataFields execution ended.");
        return categoryMetadataFieldRepository.findAll(paging);

    }

    public CategoryResponseDTO viewCategory(int id) {
        logger.info("CategoryService::viewCategory execution started.");

        Category category = categoryRepository.findById((long) id).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));

        logger.debug("CategoryService::viewCategory fetching category details");

        // converting to appropriate ResponseDTO
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(category.getId());
        categoryResponseDTO.setName(category.getName());
        categoryResponseDTO.setParent(category.getParent());

        // fetching immediate child and adding it to ResponseDTO
        Set<ChildCategoryDTO> childList = new HashSet<>();

        for(Category child: category.getChildren()){
            ChildCategoryDTO childCategoryDTO = new ChildCategoryDTO();
            childCategoryDTO.setId(child.getId());
            childCategoryDTO.setName(child.getName());
            childList.add(childCategoryDTO);

        }
        categoryResponseDTO.setChildren(childList);

        // fetching associated metadata info and adding it to ResponseDTO
        List<CategoryMetadataFieldValue> metadataList =
                categoryMetadataFieldValueRepository.findByCategory(category);

        // filtering out metadata
        List<MetadataResponseDTO> metaList = new ArrayList<>();
        for (CategoryMetadataFieldValue metadata: metadataList){
            MetadataResponseDTO metadataResponseDTO = new MetadataResponseDTO();
            metadataResponseDTO.setMetadataId(metadata.getCategoryMetadataField().getId());
            metadataResponseDTO.setFieldName(metadata.getCategoryMetadataField().getName());
            metadataResponseDTO.setPossibleValues(metadata.getValue());
            metaList.add(metadataResponseDTO);
        }
        categoryResponseDTO.setMetadataList(metaList);
        logger.info("CategoryService::viewCategory execution ended.");
        return categoryResponseDTO;

    }

    public List<CategoryResponseDTO> viewAllCategories(Pageable paging) {
        logger.info("CategoryService::viewAllCategories execution started.");
        Page<Category> categoryPage = categoryRepository.findAll(paging);
        List<CategoryResponseDTO> requiredCategories = new ArrayList<>();
        logger.debug("CategoryService::viewAllCategories converting to appropriateDTO, fetching list");
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

            // fetching associated metadata info and adding it to ResponseDTO
            List<CategoryMetadataFieldValue> metadataList =
                    categoryMetadataFieldValueRepository.findByCategory(category);

            // filtering out metadata
            List<MetadataResponseDTO> metaList = new ArrayList<>();
            for (CategoryMetadataFieldValue metadata: metadataList){
                MetadataResponseDTO metadataResponseDTO = new MetadataResponseDTO();
                metadataResponseDTO.setMetadataId(metadata.getCategoryMetadataField().getId());
                metadataResponseDTO.setFieldName(metadata.getCategoryMetadataField().getName());
                metadataResponseDTO.setPossibleValues(metadata.getValue());
                metaList.add(metadataResponseDTO);
            }
            categoryResponseDTO.setMetadataList(metaList);

            requiredCategories.add(categoryResponseDTO);
        }
        logger.info("CategoryService::viewAllCategories execution ended.");
        return requiredCategories;
    }

    public String updateCategoryName(CategoryUpdateDTO categoryUpdateDTO) {
        logger.info("CategoryService::updateCategoryName execution started.");
        Category category = categoryRepository.findById(categoryUpdateDTO.getId()).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));
        logger.debug("CategoryService::updateCategoryName updating category");
        BeanUtils.copyProperties(categoryUpdateDTO, category, FilterProperties.getNullPropertyNames(category));
        categoryRepository.save(category);
        logger.info("CategoryService::updateCategoryName execution ended.");
        return messageSource.getMessage("api.response.updateSuccess", null, Locale.ENGLISH);
    }

    public MetaFieldValueResponseDTO addMetaValues(MetaFieldValueDTO metaFieldValueDTO){

        logger.info("CategoryService::addMetaValues execution started");

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

        Optional<CategoryMetadataFieldValue> object = categoryMetadataFieldValueRepository.findById(key);
        String originalValues="";
        if(object.isPresent()){
            originalValues = object.get().getValue();
        }

        if(originalValues!=null){
            newValues = originalValues;
        }

        Optional<List<String>> check = Optional.of(List.of(originalValues.split(",")));

        for (String value : metaFieldValueDTO.getValues().stream().distinct().collect(Collectors.toList())){

            if(check.isPresent() && check.get().contains(value)){
                logger.error("CategoryService::addMetaValues an exception occurred while adding values");
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

        logger.info("CategoryService::addMetaValues execution ended");
        return metaFieldValueResponseDTO;

    }

    public List<SellerCategoryResponseDTO> viewSellerCategory(){
        logger.info("CategoryService::viewSellerCategory execution started.");
        // obtain list of categories
        List<Category> categoryList = categoryRepository.findAll();

        List<SellerCategoryResponseDTO> resultList = new ArrayList<>();

        logger.debug("CategoryService::viewSellerCategory fetch category");
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
                List<MetadataResponseDTO> metaList = new ArrayList<>();
                for (CategoryMetadataFieldValue metadata: metadataList){
                    MetadataResponseDTO metadataResponseDTO = new MetadataResponseDTO();
                    metadataResponseDTO.setMetadataId(metadata.getCategoryMetadataField().getId());
                    metadataResponseDTO.setFieldName(metadata.getCategoryMetadataField().getName());
                    metadataResponseDTO.setPossibleValues(metadata.getValue());
                    metaList.add(metadataResponseDTO);
                }
                sellerResponse.setMetadata(metaList);
                resultList.add(sellerResponse);
            }
        }
        logger.info("CategoryService::viewSellerCategory execution ended.");
        return resultList;
    }

    public Set<Category> viewCustomerCategory(Optional<Integer> optionalId){
        logger.info("CategoryService::viewCustomerCategory execution started.");
        if(optionalId.isPresent()){
            // if ID is present, fetch its immediate children
            Category category = categoryRepository.findById((long)optionalId.get()).orElseThrow(() -> new BadRequestException(
                    messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)));
            Set<Category> childList = category.getChildren();
            logger.info("CategoryService::viewCustomerCategory execution ended.");
            return childList;
        }
        else{
            // if ID isn't provided fetch all root nodes
            List<Category> categoryList = categoryRepository.findAll();
            Set<Category> rootNodes = new HashSet<>();
            // filtering rootNodes
            for(Category category: categoryList){
                if(category.getParent()==null){
                    rootNodes.add(category);
                }
            }
            logger.info("CategoryService::viewCustomerCategory execution ended.");
            return rootNodes;
        }
    }

//    public Set<Category> filterCustomerCategory(Integer id) {
//
//    }
}


