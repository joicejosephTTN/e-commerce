package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.BadRequestException;
import com.ttn.ecommerce.exception.NotFoundException;
import com.ttn.ecommerce.exception.UserNotFoundException;
import com.ttn.ecommerce.model.CustomerResponseDTO;
import com.ttn.ecommerce.model.SellerResponseDTO;
import com.ttn.ecommerce.repository.CustomerRepository;
import com.ttn.ecommerce.repository.SellerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
@RequestMapping(path = "api/admin")
public class AdminService {

    Logger logger = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    MessageSource messageSource;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmailService emailService;


    public List<CustomerResponseDTO> listAllCustomers(Integer pageNo, Integer pageSize, String sortBy,String filter){
        logger.info("AdminService::listAllCustomers execution started.");

        logger.debug("AdminService::listAllCustomers fetching list of customers");
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Customer> pagedResultCustomer = customerRepository.findAll(paging);
        if( filter!=null) {
            User user = userRepository.findUserByEmail(filter);
            if (user == null) {
                throw new NotFoundException(messageSource.getMessage("api.error.userNotFound", null, Locale.ENGLISH));
            } else {
                Customer customer = user.getCustomer();
                if(customer==null){
                    throw new NotFoundException(messageSource.getMessage("api.error.customerNotFound",null,Locale.ENGLISH));
                }
                List<CustomerResponseDTO> requiredCustomers = new ArrayList<>();
                CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
                customerResponseDTO.setUserId(user.getId());
                customerResponseDTO.setEmail(user.getEmail());
                String fullName = user.getFirstName()+" "+user.getMiddleName()+ " "+user.getLastName();
                customerResponseDTO.setFullName(fullName);
                customerResponseDTO.setActive(user.isActive());
                requiredCustomers.add(customerResponseDTO);
                logger.debug("AdminService::listAllSellers returning list of SellerResponseDTO");
                logger.info("AdminService::listAllSellers execution ended.");
                return requiredCustomers;
            }
        }
        List<CustomerResponseDTO> requiredCustomers = new ArrayList<>();


        logger.debug("AdminService::listAllCustomers converting customers to CustomerResponseDTO");
        for (Customer customer : pagedResultCustomer) {
            User user = customer.getUser();
            CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
            customerResponseDTO.setUserId(user.getId());
            customerResponseDTO.setEmail(user.getEmail());
            String fullName = user.getFirstName()+" "+user.getMiddleName()+ " "+user.getLastName();
            customerResponseDTO.setFullName(fullName);
            customerResponseDTO.setActive(user.isActive());
            requiredCustomers.add(customerResponseDTO);
        }

        logger.debug("AdminService::listAllCustomers returning list of CustomerResponseDTO");

        logger.info("AdminService::listAllCustomers execution ended.");
        return requiredCustomers;
    }


    public List<SellerResponseDTO> listAllSellers(Integer pageNo, Integer pageSize, String sortBy, String filter){
        logger.info("AdminService::listAllSellers execution started.");
        logger.debug("AdminService::listAllSellers fetching list of sellers");

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Seller> pagedResultSeller = sellerRepository.findAll(paging);

        if( filter!=null){
            User user = userRepository.findUserByEmail(filter);
            if(user==null){
                throw new NotFoundException(messageSource.getMessage("api.error.userNotFound",null,Locale.ENGLISH));
            }
            else {
                Seller seller = user.getSeller();
                if(seller==null){
                    throw new NotFoundException(messageSource.getMessage("api.error.sellerNotFound",null,Locale.ENGLISH));
                }
                List<SellerResponseDTO> requiredSellers = new ArrayList<>();
                SellerResponseDTO sellerResponseDTO = new SellerResponseDTO();
                sellerResponseDTO.setUserId(user.getId());
                sellerResponseDTO.setEmail(user.getEmail());
                String fullName = user.getFirstName()+" "+user.getMiddleName()+ " "+user.getLastName();
                sellerResponseDTO.setFullName(fullName);
                sellerResponseDTO.setActive(user.isActive());
                sellerResponseDTO.setCompanyName(seller.getCompanyName());
                sellerResponseDTO.setGst(seller.getGst());
                sellerResponseDTO.setCompanyContact(seller.getCompanyContact());
                sellerResponseDTO.setAddress(seller.getAddress());
                requiredSellers.add(sellerResponseDTO);
                logger.debug("AdminService::listAllSellers returning list of SellerResponseDTO");
                logger.info("AdminService::listAllSellers execution ended.");
                return requiredSellers;
            }
        }

        List<SellerResponseDTO> requiredSellers = new ArrayList<>();

        logger.debug("AdminService::listAllSellers converting sellers to SellerResponseDTO");
        for (Seller seller : pagedResultSeller) {
            User user = seller.getUser();
            SellerResponseDTO sellerResponseDTO = new SellerResponseDTO();
            sellerResponseDTO.setUserId(user.getId());
            sellerResponseDTO.setEmail(user.getEmail());
            String fullName = user.getFirstName()+" "+user.getMiddleName()+ " "+user.getLastName();
            sellerResponseDTO.setFullName(fullName);
            sellerResponseDTO.setActive(user.isActive());
            sellerResponseDTO.setCompanyName(seller.getCompanyName());
            sellerResponseDTO.setGst(seller.getGst());
            sellerResponseDTO.setCompanyContact(seller.getCompanyContact());
            sellerResponseDTO.setAddress(seller.getAddress());
            requiredSellers.add(sellerResponseDTO);
        }

        logger.debug("AdminService::listAllSellers returning list of SellerResponseDTO");

        logger.info("AdminService::listAllSellers execution ended.");
        return requiredSellers;
    }


    public String activateUser(Long userId){
        logger.info("AdminService::activateUser execution started.");
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            if(user.get().isActive()){
                logger.debug("AdminService::activateUser account already active");
                logger.info("AdminService::activateUser execution ended.");
                return messageSource.getMessage("api.response.userAlreadyActive",null, Locale.ENGLISH);
            }
            else {
                logger.debug("AdminService::activateUser activating account");
                user.get().setActive(true);
                userRepository.save(user.get());
                emailService.sendIsActivatedMail(user.get());
                logger.info("AdminService::activateUser execution ended.");
                return messageSource.getMessage("api.response.userAccountActivated",null, Locale.ENGLISH);
            }
        }
        else{
            logger.error("AdminService::activateUser exception occurred while activating account");
            throw new NotFoundException(messageSource.getMessage("api.error.userNotFound",null, Locale.ENGLISH));
        }

    }

    public String deactivateUser(Long userId){
        logger.info("AdminService::deactivateUser execution started.");
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            if(user.get().isActive()){
                logger.debug("AdminService::deactivateUser deactivating account");
                user.get().setActive(false);
                userRepository.save(user.get());
                emailService.sendDeActivatedMail(user.get());
                logger.info("AdminService::deactivateUser execution ended.");
                return messageSource.getMessage("api.response.userAccountDeactivated",null, Locale.ENGLISH);
            }
            else {
                logger.debug("AdminService::deactivateUser account already deactivated");
                logger.info("AdminService::deactivateUser execution ended.");
                return messageSource.getMessage("api.response.userAlreadyDeactivated",null, Locale.ENGLISH);
            }
        }
        else{
            logger.error("AdminService::deactivateUser exception occurred while deactivating account");
            throw new NotFoundException(messageSource.getMessage("api.error.userNotFound",null, Locale.ENGLISH));
        }

    }

}
