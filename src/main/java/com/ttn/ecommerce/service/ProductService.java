package com.ttn.ecommerce.service;
import com.ttn.ecommerce.entity.*;
import com.ttn.ecommerce.exception.BadRequestException;
import com.ttn.ecommerce.model.ProductDTO;
import com.ttn.ecommerce.model.ProductResponseDTO;
import com.ttn.ecommerce.model.ProductUpdateDTO;
import com.ttn.ecommerce.model.VariationDTO;
import com.ttn.ecommerce.repository.*;
import com.ttn.ecommerce.utils.FilterProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    CategoryMetadataFieldRepository metadataFieldRepository;

    @Autowired
    ProductVariationRepository productVariationRepository;

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
        product.setCancellable(false);
        product.setReturnable(false);
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

    public String addVariation(VariationDTO addVariationDTO){

        // validate ProductId

        Optional<Product> product = productRepository.findById(addVariationDTO.getProductId());
        if(product.isEmpty()){
            throw new BadRequestException(messageSource.getMessage("api.error.invalidId",null,Locale.ENGLISH));
        }

        // check product status
        if (product.get().isActive() || product.get().isDeleted()){
            throw new BadRequestException(messageSource.getMessage("api.error.productInactiveDeleted",null,Locale.ENGLISH));
        }

        Category associatedCategory = product.get().getCategory();

        // check if provided field and their values
        // are among the existing metaField-values defined for the category

        Map<String, Set<String>> requestMetadata = addVariationDTO.getMetadata();
        List<String> requestKeySet = requestMetadata.keySet().stream().collect(Collectors.toList());

        List<CategoryMetadataFieldValue> associatedMetadata = metadataFieldValueRepository.findByCategory(associatedCategory);
        List<String> associatedKeySet = new ArrayList<>();

        for(CategoryMetadataFieldValue metadataFieldValue : associatedMetadata){
            CategoryMetadataField field = metadataFieldValue.getCategoryMetadataField();
            String fieldName = field.getName();
            associatedKeySet.add(fieldName);
        }

        // check if metadataField are associated with the category
        if(Collections.indexOfSubList(associatedKeySet,requestKeySet)==-1){
            requestKeySet.removeAll(associatedKeySet);
            String errorResponse = messageSource.getMessage("api.error.fieldNotAssociated",null,Locale.ENGLISH);
            errorResponse=errorResponse.replace("[[fields]]", requestKeySet.toString());
            throw new BadRequestException(errorResponse);
        }

        // check if metadataValues are associated for given category, metadataField
        for(String key: requestKeySet){
            CategoryMetadataField categoryMetadataField = metadataFieldRepository.findByNameIgnoreCase(key);
            CategoryMetadataFieldValue categoryMetadataFieldValue = metadataFieldValueRepository.findByCategoryAndCategoryMetadataField(associatedCategory,categoryMetadataField);
            String associatedStringValues = categoryMetadataFieldValue.getValue();
            String associatedField = categoryMetadataFieldValue.getCategoryMetadataField().getName();
            Set<String> associatedValues =Set.of(associatedStringValues.split(","));
            Set<String> requestValues = requestMetadata.get(key);

            if(associatedValues.containsAll(requestValues)==false){
                requestValues.removeAll(associatedValues);
                String errorResponse = messageSource.getMessage("api.error.valueNotAssociated",null,Locale.ENGLISH);
                errorResponse=errorResponse.replace("[[value]]",associatedField+"-"+requestValues);
                throw new BadRequestException(errorResponse);
            }
        }
        ProductVariation productVariation = new ProductVariation();
        BeanUtils.copyProperties(addVariationDTO,productVariation);
        productVariation.setProduct(product.get());
        productVariationRepository.save(productVariation);

        return messageSource.getMessage("api.response.addedSuccess",null,Locale.ENGLISH);

    }

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
            productResponseDTO.setIsActive(product.get().isActive());
            productResponseDTO.setIsReturnable(product.get().isReturnable());
            productResponseDTO.setIsCancellable(product.get().isCancellable());
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
            productResponseDTO.setIsActive(product.isActive());
            productResponseDTO.setIsReturnable(product.isCancellable());
            productResponseDTO.setIsCancellable(product.isCancellable());
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


    public ProductResponseDTO adminViewProduct(Long id){
        // check if ID is valid
        Optional<Product> product = productRepository.findById(id);
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }

        // convert to appropriate DTO
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.get().getId());
        productResponseDTO.setName(product.get().getName());
        productResponseDTO.setBrand(product.get().getBrand());
        productResponseDTO.setDescription(product.get().getDescription());
        productResponseDTO.setIsActive(product.get().isActive());
        productResponseDTO.setIsReturnable(product.get().isReturnable());
        productResponseDTO.setIsCancellable(product.get().isCancellable());
        productResponseDTO.setCategory(product.get().getCategory());

        return productResponseDTO;
    }

    public List<ProductResponseDTO> adminViewAllProducts(){
        // fetch all products
        List<Product> products = productRepository.findAll();
        // check if records are present
        if(products.isEmpty()){
            throw new BadRequestException(messageSource.getMessage("api.error.productNotFound",null,Locale.ENGLISH));
        }

        // convert to appropriate DTO
        List<ProductResponseDTO> productResponseDTOList= new ArrayList<>();
        for(Product product: products){

            ProductResponseDTO productResponseDTO = new ProductResponseDTO();

            productResponseDTO.setId(product.getId());
            productResponseDTO.setName(product.getName());
            productResponseDTO.setBrand(product.getBrand());
            productResponseDTO.setDescription(product.getDescription());
            productResponseDTO.setIsActive(product.isActive());
            productResponseDTO.setIsCancellable(product.isCancellable());
            productResponseDTO.setIsReturnable(product.isReturnable());
            productResponseDTO.setCategory(product.getCategory());
            productResponseDTOList.add(productResponseDTO);
        }
        return productResponseDTOList;

    }

    public String activateProduct(Long id){
        // check if ID is valid
        Optional<Product> product = productRepository.findById(id);
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }
        if (product.get().isActive()){
            throw new BadRequestException(messageSource.getMessage("api.error.productAlreadyActive",null,Locale.ENGLISH));

        }else{
            // update status & save
            product.get().setActive(true);
            productRepository.save(product.get());

            // trigger mail
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            BeanUtils.copyProperties(product.get(),productResponseDTO);
            User productOwner = product.get().getSeller().getUser();
            emailService.sendProductActivationMail(productResponseDTO,productOwner);

            return messageSource.getMessage("api.response.activationSuccess",null,Locale.ENGLISH);
        }

    }


    public String deactivateProduct(Long id){
        // check if ID is valid
        Optional<Product> product = productRepository.findById(id);
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }
        if (product.get().isActive()==false){
            throw new BadRequestException(messageSource.getMessage("api.error.productAlreadyInactive",null,Locale.ENGLISH));

        }else{
            // update status & save
            product.get().setActive(false);
            productRepository.save(product.get());

            // trigger mail
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            BeanUtils.copyProperties(product.get(), productResponseDTO);
            User productOwner = product.get().getSeller().getUser();
            emailService.sendProductDeactivationMail(productResponseDTO,productOwner);

            return messageSource.getMessage("api.response.deactivationSuccess",null,Locale.ENGLISH);
        }

    }


    public ProductResponseDTO customerViewProduct(Long productId){
        // check if ID is valid
        Optional<Product> product = productRepository.findById(productId);
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }
        // check product status
        if (product.get().isDeleted() || product.get().isActive() == false ){
            throw new BadRequestException(messageSource.getMessage("api.error.productInactiveDeleted",null,Locale.ENGLISH));
        }

        // convert to appropriate DTO
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.get().getId());
        productResponseDTO.setName(product.get().getName());
        productResponseDTO.setBrand(product.get().getBrand());
        productResponseDTO.setDescription(product.get().getDescription());
        productResponseDTO.setIsActive(product.get().isActive());
        productResponseDTO.setIsReturnable(product.get().isReturnable());
        productResponseDTO.setIsCancellable(product.get().isCancellable());
        productResponseDTO.setCategory(product.get().getCategory());

        return productResponseDTO;
    }

    public List<ProductResponseDTO> customerViewAllProducts(Long categoryId){

        // check if category ID is valid
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException(
                messageSource.getMessage("api.error.invalidId", null, Locale.ENGLISH)
        ));

        // check if category is a leaf node
        if (category.getChildren()!=null){
            throw new BadRequestException(messageSource.getMessage("api.error.notLeafNode",null,Locale.ENGLISH));
        }

         List<Product> products = productRepository.findByCategory(category);
        // check if records are present
        if(products.isEmpty()){
            throw new BadRequestException(messageSource.getMessage("api.error.productNotFound",null,Locale.ENGLISH));
        }

        // convert to appropriate DTO
        List<ProductResponseDTO> productResponseDTOList= new ArrayList<>();
        for(Product product: products){

            ProductResponseDTO productResponseDTO = new ProductResponseDTO();

            productResponseDTO.setId(product.getId());
            productResponseDTO.setName(product.getName());
            productResponseDTO.setBrand(product.getBrand());
            productResponseDTO.setDescription(product.getDescription());
            productResponseDTO.setIsActive(product.isActive());
            productResponseDTO.setIsCancellable(product.isCancellable());
            productResponseDTO.setIsReturnable(product.isReturnable());
            productResponseDTO.setCategory(product.getCategory());
            productResponseDTOList.add(productResponseDTO);
        }
        return productResponseDTOList;

    }

    public List<Product> viewSimilarProducts(Long productId){

        // check if ID is valid
        Optional<Product> product = productRepository.findById(productId);
        if (product == null || product.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("api.error.invalidProductId",null,Locale.ENGLISH));
        }

        // check product status
        if (product.get().isDeleted() || product.get().isActive() == false ){
            throw new BadRequestException(messageSource.getMessage("api.error.productInactiveDeleted",null,Locale.ENGLISH));
        }

        // find similar products
        Category associatedCategory = product.get().getCategory();
        List<Product> similarProducts = new ArrayList<>();

        // add other products associated to its category to similar list
        List<Product> siblingProducts = productRepository.findByCategory(associatedCategory);

        for(Product individualProduct: siblingProducts){
            similarProducts.add(individualProduct);
        }

        if(similarProducts.size()==1){
            throw new BadRequestException(messageSource.getMessage("api.error.similarProducts",null,Locale.ENGLISH));
        }

        return similarProducts;

    }



}
