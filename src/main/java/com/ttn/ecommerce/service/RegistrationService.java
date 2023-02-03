package com.ttn.ecommerce.service;
import com.ttn.ecommerce.exception.BadRequestException;
import com.ttn.ecommerce.exception.PasswordDoNotMatchException;
import com.ttn.ecommerce.exception.UserAlreadyExistsException;
import com.ttn.ecommerce.entity.*;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.CustomerDTO;
import com.ttn.ecommerce.model.SellerDTO;
import com.ttn.ecommerce.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class RegistrationService {
    Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    MessageSource messageSource;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    EmailService emailService;


    @Transactional
    public String createSeller(SellerDTO sellerDTO){
        logger.info("RegistrationService::createSeller execution started.");

        // checking if username(email) already exists
        String providedEmail = sellerDTO.getEmail();
        User existingUser = userRepository.findUserByEmail(providedEmail);
        if(existingUser!=null){
            logger.error("RegistrationService::createSeller Exception occurred while persisting seller to the database");
            throw new UserAlreadyExistsException(messageSource.getMessage("api.error.userEmailExists",null, Locale.ENGLISH));
        }
        // checking if password and reEnterPassword match
        else if( !(sellerDTO.getPassword().equals(sellerDTO.getReEnterPassword())) ){
            logger.error("RegistrationService::createSeller Exception occurred while persisting seller to the database");
            throw new PasswordDoNotMatchException(messageSource.getMessage("api.error.passwordDoNotMatch",null, Locale.ENGLISH));
        }
        else {
            User user = new User();
            user.setFirstName(sellerDTO.getFirstName());
            user.setLastName(sellerDTO.getLastName());
            //optional
            user.setMiddleName(sellerDTO.getMiddleName());
            user.setEmail(providedEmail);
            user.setPassword(bCryptPasswordEncoder.encode(sellerDTO.getReEnterPassword()));

            Role role = roleRepository.findRoleByAuthority("SELLER");
            user.setRole(role);

            Seller seller = new Seller();
            String providedGst = sellerDTO.getGst();
            Seller existingGst = sellerRepository.findByGst(providedGst);

            String providedCompanyName = sellerDTO.getCompanyName();
            Seller existingCompanyName = sellerRepository.findByCompanyNameIgnoreCase(providedCompanyName);
            if(existingGst!=null){
                logger.error("RegistrationService::createSeller Exception occurred while persisting seller to the database");
                throw new BadRequestException(messageSource.getMessage("api.error.userGstExists",null, Locale.ENGLISH));
            }
            else if(existingCompanyName!=null) {
                logger.error("RegistrationService::createSeller Exception occurred while persisting seller to the database");
                throw new BadRequestException(messageSource.getMessage("api.error.userCompanyExists",null, Locale.ENGLISH));
            }
            else {
                logger.debug("RegistrationService::createSeller persisting seller to the database");
                seller.setGst(sellerDTO.getGst());
                seller.setCompanyName(sellerDTO.getCompanyName());
                seller.setCompanyContact(sellerDTO.getCompanyContact());

                seller.setUser(user);

                AddressDTO addressDTO = sellerDTO.getAddress();

                Address address = new Address();
                address.setCity(addressDTO.getCity());
                address.setState(addressDTO.getState());
                address.setAddressLine(addressDTO.getAddressLine());
                address.setZipCode(addressDTO.getZipCode());
                address.setCountry(addressDTO.getCountry());
                address.setLabel(addressDTO.getLabel());

                address.setSeller(seller);

                addressRepository.save(address);
                sellerRepository.save(seller);
                userRepository.save(user);

                // sending mail to notify account creation
                emailService.sendAwaitingApprovalMail(user);
            }

        }
        logger.info("RegistrationService::createSeller execution ended.");

        return messageSource.getMessage("api.response.sellerRegistered",null, Locale.ENGLISH);
    }

    @Transactional
    public String createCustomer(CustomerDTO customerDTO){
        logger.info("RegistrationService::createCustomer execution started.");

        // checking if username(email) already exists
        String providedEmail = customerDTO.getEmail();
        User existingUser = userRepository.findUserByEmail(providedEmail);
        if(existingUser!=null){
            logger.error("Exception occurred while persisting customer to the database");
            throw new BadRequestException(messageSource.getMessage("api.error.userEmailExists",null, Locale.ENGLISH));
        }
        // checking if password and reEnterPassword match
        else if( !(customerDTO.getPassword().equals(customerDTO.getReEnterPassword())) ){
            logger.error("Exception occurred while persisting customer to the database");
            throw new BadRequestException(messageSource.getMessage("api.error.passwordDoNotMatch",null, Locale.ENGLISH));
        }
        else {

            logger.debug("RegistrationService::createCustomer persisting customer to the database and sending email");
            User user = new User();
            user.setFirstName(customerDTO.getFirstName());
            user.setLastName(customerDTO.getLastName());

            //optional
            user.setMiddleName(customerDTO.getMiddleName());
            user.setEmail(customerDTO.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(customerDTO.getReEnterPassword()));

            Role role = roleRepository.findRoleByAuthority("CUSTOMER");
            user.setRole(role);

            Customer customer = new Customer();
            customer.setContact(customerDTO.getContact());
            customer.setUser(user);
            customerRepository.save(customer);

            customerDTO.getAddress().forEach(addressDTO -> {
                Address address = new Address();
                address.setCity(addressDTO.getCity());
                address.setState(addressDTO.getState());
                address.setAddressLine(addressDTO.getAddressLine());
                address.setZipCode(addressDTO.getZipCode());
                address.setCountry(addressDTO.getCountry());
                address.setLabel(addressDTO.getLabel());
                address.setCustomer(customer);

                addressRepository.save(address);

            });
            userRepository.save(user);
            // sending an activation email
            emailService.sendActivationMail(user);
        }
        logger.info("RegistrationService::createCustomer execution ended.");
        return messageSource.getMessage("api.response.customerRegistered",null, Locale.ENGLISH);
    }

}
