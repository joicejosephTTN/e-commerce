package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.InvalidFileFormatException;
import com.ttn.ecommerce.exception.PasswordDoNotMatchException;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.AddressUpdateDTO;
import com.ttn.ecommerce.model.SellerUpdateDTO;
import com.ttn.ecommerce.model.SellerViewDTO;
import com.ttn.ecommerce.repository.AddressRepository;
import com.ttn.ecommerce.repository.SellerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;


@Service
public class SellerService {
    Logger logger = LoggerFactory.getLogger(SellerService.class);

    @Autowired
    MessageSource messageSource;
    @Autowired
    ImageService imageService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SellerRepository sellerRepository;


    public SellerViewDTO fetchProfile(String email) {
        logger.info("SellerService::fetchProfile execution started.");
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();

        SellerViewDTO sellerViewDTO = new SellerViewDTO();
        logger.debug("SellerService::fetchProfile fetching seller details and converting to appropriate DTO");

        // converting seller to appropriate responseDTO
        sellerViewDTO.setUserId(user.getId());
        sellerViewDTO.setFirstName(user.getFirstName());
        sellerViewDTO.setLastName(user.getLastName());

        // converting Address to AddressDTO
        Address address = seller.getAddress();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressLine(address.getAddressLine());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setLabel(address.getLabel());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setZipCode(address.getZipCode());

        // converting seller to appropriate responseDTO
        sellerViewDTO.setAddress(addressDTO);
        sellerViewDTO.setActive(user.isActive());
        sellerViewDTO.setGst(seller.getGst());
        sellerViewDTO.setCompanyName(seller.getCompanyName());
        sellerViewDTO.setCompanyContact(seller.getCompanyContact());

        // fetch image if present and add it to customerViewDTO
        byte[] associatedImage = imageService.getImage(email);
        if(associatedImage.length !=0){
            sellerViewDTO.setImage(imageService.getImage(email));
        }
        logger.info("SellerService::fetchProfile execution ended.");
        return sellerViewDTO;
    }

    public String updateProfile(String email, SellerUpdateDTO sellerUpdateDTO, MultipartFile image) {
        logger.info("SellerService::updateProfile execution started.");
        // get associated user,seller and address
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();
        Address address = seller.getAddress();
        logger.debug("SellerService::updateProfile fetching details from request");

        // extract addressDTO object from the incoming request
        AddressUpdateDTO addressDTO = sellerUpdateDTO.getAddress();
        logger.debug("SellerService::updateProfile updating details");

        //partial update of address - ignoring null properties received in request
        BeanUtils.copyProperties(addressDTO, address, FilterProperties.getNullPropertyNames(addressDTO));

        // saving updates
        address.setSeller(seller);
        addressRepository.save(address);

        // partial updates
        BeanUtils.copyProperties(sellerUpdateDTO, user, FilterProperties.getNullPropertyNames(sellerUpdateDTO));
        BeanUtils.copyProperties(sellerUpdateDTO, seller, FilterProperties.getNullPropertyNames(sellerUpdateDTO));

        // take the image, save it or replace if it exists
        if(!image.isEmpty()) {
            if ((image.getContentType().equals("image/jpg")
                    || image.getContentType().equals("image/jpeg")
                    || image.getContentType().equals("image/bmp")
                    || image.getContentType().equals("image/png"))) {
                try {
                    imageService.saveImage(email, image);
                    userRepository.save(user);
                    sellerRepository.save(seller);
                    logger.info("SellerService::updateProfile execution ended.");
                    return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);

                } catch (IOException e) {
                    logger.error("SellerService::updateProfile An exception occurred while updating profile");
                    throw new RuntimeException(e);
                }
            } else {
                logger.error("SellerService::updateProfile An exception occurred while updating profile");
                throw new InvalidFileFormatException(messageSource.getMessage("api.error.invalidFileType",null, Locale.ENGLISH));
            }
        }
        // saving updates
        userRepository.save(user);
        sellerRepository.save(seller);
        logger.info("SellerService::updateProfile execution ended.");
        return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);

    }

    public String updatePassword(String email, String password, String confirmPass) {
        logger.info("SellerService::updatePassword execution started.");
        User user = userRepository.findUserByEmail(email);
        if (!password.equals(confirmPass)) {
            logger.error("SellerService::updatePassword an exception occurred while updating");
            throw new PasswordDoNotMatchException(messageSource.getMessage("api.error.passwordDoNotMatch",null, Locale.ENGLISH));
        }
        logger.debug("SellerService::updatePassword updating password");
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendSuccessfulChangeMail(user);
        logger.info("SellerService::updatePassword execution ended.");
        return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);
    }
}

