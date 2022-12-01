package com.ttn.ecommerce.service;
import com.ttn.ecommerce.entity.*;
import com.ttn.ecommerce.exception.BadRequestException;
import com.ttn.ecommerce.model.ProductDTO;
import com.ttn.ecommerce.model.ProductResponseDTO;
import com.ttn.ecommerce.model.ProductUpdateDTO;
import com.ttn.ecommerce.repository.CategoryMetadataFieldValueRepository;
import com.ttn.ecommerce.repository.CategoryRepository;
import com.ttn.ecommerce.repository.ProductRepository;
import com.ttn.ecommerce.repository.UserRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    EmailService emailService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CategoryMetadataFieldValueRepository metadataFieldValueRepository;

    public String addProduct(Authentication authentication, ProductDTO productDTO){

        // fetch associated details
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();

        String brandName = productDTO.getBrand();
        Category associatedCategory = categoryRepository.findById((long) productDTO.getCategoryId()).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));

        if(!associatedCategory.getChildren().isEmpty()){
            throw new BadRequestException(messageSource.getMessage("api.error.notLeafNode", null, Locale.ENGLISH));
        }

        String productName = productDTO.getName();
        String productDescription = productDTO.getDescription();

        // check to see if name has already been
        // used for (seller, category, brand) combo
        Optional<Product> existingProduct
                = productRepository.findByNameAndSellerAndCategoryAndBrand(
                        productName,seller,associatedCategory,brandName
        );
        if (existingProduct.isPresent()){
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductName",null, Locale.ENGLISH));
        }

        // save product
        Product product = new Product();
        product.setActive(false);
        product.setName(productName);
        product.setDescription(productDescription);
        product.setBrand(brandName);
        product.setCategory(associatedCategory);
        product.setSeller(seller);
        productRepository.save(product);

        //trigger e-mail
        emailService.sendNewProductMail(product);

        return messageSource.getMessage("api.response.addedSuccess",null,Locale.ENGLISH);
    }

//    public String addVariation(VariationDTO addVariationDTO){
//        // validate ProductId
//        Optional<Product> product = productRepository.findById(addVariationDTO.getProductId());
//        if(product.isEmpty()){
//            throw new BadRequestException(messageSource.getMessage("api.error.invalidId",null,Locale.ENGLISH));
//        }
//        // check product status
//        if (product.get().isActive() || product.get().isDeleted()){
//            throw new BadRequestException(messageSource.getMessage("api.error.productInactiveDeleted",null,Locale.ENGLISH));
//        }
//
//        Category associatedCategory = product.get().getCategory();
//
//        // check if provided field and their values
//        // are among the existing metaField-values defined for the category
//
//        Map<String, Set<String>> requestMetadata = addVariationDTO.getMetadata();
//        List<String> requestKeySet = requestMetadata.keySet().stream().collect(Collectors.toList());
//
//
//        List<CategoryMetadataFieldValue> associatedMetadata = metadataFieldValueRepository.findByCategory(associatedCategory);
//        List<String> associatedKeySet = new ArrayList<>();
//
//        for(CategoryMetadataFieldValue metadataFieldValue : associatedMetadata){
//            CategoryMetadataField field = metadataFieldValue.getCategoryMetadataField();
//            String fieldName = field.getName();
//            associatedKeySet.add(fieldName);
//        }
//
//        // check if metadataField are associated with the category
//        if(!associatedKeySet.contains(requestKeySet)){
//            requestKeySet.removeAll(associatedKeySet);
//            String errorResponse = messageSource.getMessage("api.error.fieldNotAssociated",null,Locale.ENGLISH);
//            errorResponse.replace("[[fields]]",requestKeySet.toString());
//            throw new BadRequestException(errorResponse);
//        }
//
//        // check if metadataValues are associated for given category, metadataField
//
//        }

    public ProductResponseDTO viewProduct(Authentication authentication, Long id){
        Optional<Product> product = productRepository.findById(id);
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }

        User user = userRepository.findUserByEmail(authentication.getName());

        if(product.get().getSeller().getId() == user.getId()){
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setId(product.get().getId());
            productResponseDTO.setName(product.get().getName());
            productResponseDTO.setBrand(product.get().getBrand());
            productResponseDTO.setDescription(product.get().getDescription());
            productResponseDTO.setActive(product.get().isActive());
            productResponseDTO.setCategory(product.get().getCategory());
            return productResponseDTO;
        } else {
            throw new BadRequestException(messageSource.getMessage("api.error.sellerNotAssociated",null,Locale.ENGLISH));
        }
    }

    public List<ProductResponseDTO> viewAllProducts(Authentication authentication){

        User user= userRepository.findUserByEmail(authentication.getName());
        Seller seller = user.getSeller();
        List<Product> products = productRepository.findBySeller(seller);
        if(products.isEmpty()){
            throw new BadRequestException(messageSource.getMessage("api.error.productNotAssociated",null,Locale.ENGLISH));
        }
        List<ProductResponseDTO> productResponseDTOList= new ArrayList<>();
        for(Product product: products){

            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setId(product.getId());
            productResponseDTO.setName(product.getName());
            productResponseDTO.setBrand(product.getBrand());
            productResponseDTO.setDescription(product.getDescription());
            productResponseDTO.setActive(product.isActive());

            productResponseDTO.setCategory(product.getCategory());
            productResponseDTOList.add(productResponseDTO);

        }
        return productResponseDTOList;
    }

    public String deleteProduct(Authentication authentication, Long id){

        // fetch product
        Optional<Product> product = productRepository.findById(id);
        // check if ID is valid
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }

        User user = userRepository.findUserByEmail(authentication.getName());
        Seller productSeller = product.get().getSeller();
        // check if product belongs to the user deleting it
        if(productSeller == user.getSeller()){
            productRepository.delete(product.get());
            return messageSource.getMessage("api.response.deleteSuccess",null,Locale.ENGLISH);
        } else{
            throw new BadRequestException(messageSource.getMessage("api.error.sellerNotAssociated",null,Locale.ENGLISH));
        }

    }

    public String updateProduct(Long id, Authentication authentication, ProductUpdateDTO productUpdateDTO) {
        // fetch product
        Optional<Product> product = productRepository.findById(id);
        // check if ID is valid
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId", null, Locale.ENGLISH));
        }

        User user = userRepository.findUserByEmail(authentication.getName());
        Seller productSeller = product.get().getSeller();
        // check if product belongs to the user updating it
        if(productSeller == user.getSeller()){
            Product newProduct = product.get();
            // update & save
            BeanUtils.copyProperties(productUpdateDTO,newProduct,FilterProperties.getNullPropertyNames(newProduct));
            productRepository.save(newProduct);

            return messageSource.getMessage("api.response.updateSuccess", null, Locale.ENGLISH);
        } else {
            throw new BadRequestException(messageSource.getMessage("api.error.sellerNotAssociated", null, Locale.ENGLISH));
        }
    }



}
