package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.*;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.AddressUpdateDTO;
import com.ttn.ecommerce.model.CustomerUpdateDTO;
import com.ttn.ecommerce.model.CustomerViewDTO;
import com.ttn.ecommerce.repository.AddressRepository;
import com.ttn.ecommerce.repository.CustomerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CustomerService {

    Logger logger = LoggerFactory.getLogger(CustomerService.class);

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
    CustomerRepository customerRepository;

    public CustomerViewDTO fetchProfile(String email) {
        logger.info("CustomerService::fetchProfile execution started.");

        logger.debug("CustomerService::fetchProfile gathering details to display on profile");
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();

        CustomerViewDTO customerViewDTO = new CustomerViewDTO();

        logger.debug("CustomerService::fetchProfile converting to CustomerViewDTO");
        // converting seller to appropriate responseDTO
        customerViewDTO.setUserId(user.getId());
        customerViewDTO.setFirstName(user.getFirstName());
        customerViewDTO.setLastName(user.getLastName());
        customerViewDTO.setActive(user.isActive());
        customerViewDTO.setContact(customer.getContact());

        // fetch image if present and add it to customerViewDTO
        byte[] associatedImage = imageService.getImage(email);
        if(associatedImage.length !=0){
            customerViewDTO.setImage(imageService.getImage(email));
        }

        logger.debug("CustomerService::fetchProfile displaying details");
        logger.info("CustomerService::fetchProfile execution ended.");
        return customerViewDTO;
    }


    public String updateProfile(String email, CustomerUpdateDTO customerUpdateDTO, MultipartFile image) {

        logger.info("CustomerService::updateProfile execution started.");

        logger.debug("CustomerService::updateProfile updating provided details");
        // getting associated user and customer
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();

        if(customerUpdateDTO!=null) {
            // partial updates
            BeanUtils.copyProperties(customerUpdateDTO, user, FilterProperties.getNullPropertyNames(customerUpdateDTO));
            BeanUtils.copyProperties(customerUpdateDTO, customer, FilterProperties.getNullPropertyNames(customerUpdateDTO));
        }
        // take the image, save it or replace if it exists
        if(!image.isEmpty()) {
            if ((image.getContentType().equals("image/jpg")
                    || image.getContentType().equals("image/jpeg")
                    || image.getContentType().equals("image/bmp")
                    || image.getContentType().equals("image/png"))) {
                try {
                    logger.debug("CustomerService::updateProfile saving changes");
                    imageService.saveImage(email, image);
                    userRepository.save(user);
                    customerRepository.save(customer);
                    logger.info("CustomerService::updateProfile execution ended.");
                    return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);

                } catch (IOException e) {
                    logger.error("CustomerService::updateProfile An exception occurred while uploading image");
                    throw new RuntimeException(e);
                }
            } else {
                logger.error("CustomerService::updateProfile An exception occurred while uploading image");
                throw new InvalidFileFormatException(messageSource.getMessage("api.error.invalidFileType",null, Locale.ENGLISH));
            }
        }

        // saving updates
        logger.debug("CustomerService::updateProfile saving changes");
        userRepository.save(user);
        customerRepository.save(customer);
        logger.info("CustomerService::updateProfile execution ended.");
        return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);
    }

    public String updatePassword(String email, String password, String confirmPass){
        logger.info("CustomerService::updatePassword execution started.");

        logger.debug("CustomerService::updatePassword fetching details from request");
        User user = userRepository.findUserByEmail(email);
        if(!password.equals(confirmPass)){
            logger.error("CustomerService::updatePassword An Exception occurred while updating the password");
            throw new PasswordDoNotMatchException(messageSource.getMessage("api.error.passwordDoNotMatch",null, Locale.ENGLISH));
        }
        logger.debug("CustomerService::updatePassword setting up the new password");
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendSuccessfulChangeMail(user);
        logger.info("CustomerService::updatePassword execution ended.");
        return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);
    }

    public List<Address> fetchAddress(String email){
        logger.info("CustomerService::fetchAddress execution started.");
        logger.debug("CustomerService::fetchAddress fetching list of associated address");
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();
        List<Address> address = customer.getAddress();
        logger.info("CustomerService::fetchAddress execution ended.");
        return address;
    }

    public String addAddress(String email, AddressDTO addressDTO){
        logger.info("CustomerService::addAddress execution started.");
        logger.debug("CustomerService::addAddress converting request to AddressDTO");
        // fetch associated user, customer
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();

        // convert requestDTO to address object
        Address address = new Address();
        address.setCustomer(customer); // set customer for the address object
        address.setAddressLine(addressDTO.getAddressLine());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setZipCode(addressDTO.getZipCode());

        logger.debug("CustomerService::addAddress saving the new address");
        // save the address
        addressRepository.save(address);
        logger.debug("CustomerService::addAddress execution ended.");
        return messageSource.getMessage("api.response.addressAdd",null, Locale.ENGLISH);
    }


    public String deleteAddress(String email, Long addressId){
        logger.info("CustomerService::deleteAddress execution started.");
        logger.debug("CustomerService::deleteAddress fetching the associated address");
        // getting associated user, customer
        User user = userRepository.findUserByEmail(email);
        Customer loggedCustomer = user.getCustomer();
        // getting requested address
        Optional<Address> address = addressRepository.findById(addressId);

        if(address.isPresent()){
            Address reqdAddress = address.get();
            // check to see if address is associated with logged in customer
            if (reqdAddress.getCustomer() == loggedCustomer){
                logger.debug("CustomerService::deleteAddress deleting the address");
                addressRepository.delete(reqdAddress);
                logger.info("CustomerService::deleteAddress execution ended.");
                return messageSource.getMessage("api.response.addressDelete",null, Locale.ENGLISH);
            }
            logger.error("CustomerService::deleteAddress exception occurred while deleting");
            throw new BadRequestException(messageSource.getMessage("api.error.invalidId",null, Locale.ENGLISH));

        }
        logger.error("CustomerService::deleteAddress exception occurred while deleting");
        throw new NotFoundException(messageSource.getMessage("api.error.addressNotFound",null, Locale.ENGLISH));
    }

    public String updateAddress(String email, Long addressId, AddressUpdateDTO addressDTO){
        logger.info("CustomerService::updateAddress execution started.");
        logger.debug("CustomerService::updateAddress fetching the associated address");
        // if empty addressDTO is being sent in request
        AddressUpdateDTO emptyAddressDTO = new AddressUpdateDTO();
        if(addressDTO.equals(emptyAddressDTO)){
            return messageSource.getMessage("api.response.noUpdate",null, Locale.ENGLISH);
        }
        // getting associated user, customer
        User user = userRepository.findUserByEmail(email);
        Customer loggedCustomer = user.getCustomer();

        // getting requested address
        Optional<Address> address = addressRepository.findById(addressId);


        logger.debug("CustomerService::updateAddress making updating details of address");

        if(address.isPresent()){
            Address reqdAddress = address.get();
            // check to see if address is associated with logged in customer
            if (reqdAddress.getCustomer() == loggedCustomer){

                //partial update of address - ignoring null properties received in request
                BeanUtils.copyProperties(addressDTO, reqdAddress, FilterProperties.getNullPropertyNames(addressDTO));


                logger.debug("CustomerService::updateAddress saving address");
                // saving updates
                reqdAddress.setCustomer(loggedCustomer);
                addressRepository.save(reqdAddress);

                logger.info("CustomerService::updateAddress execution ended.");
                return messageSource.getMessage("api.response.updateSuccess",null, Locale.ENGLISH);
            }
            logger.error("CustomerService::updateAddress exception occurred while updating the address");
            throw new NotFoundException(messageSource.getMessage("api.error.addressNotFound",null, Locale.ENGLISH));
        }
        logger.error("CustomerService::updateAddress exception occurred while updating the address");
        throw new NotFoundException(messageSource.getMessage("api.error.addressNotFound",null, Locale.ENGLISH));
    }
}
