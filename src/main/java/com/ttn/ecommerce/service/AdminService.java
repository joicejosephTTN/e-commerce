package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.UserNotFoundException;
import com.ttn.ecommerce.model.CustomerResponseDTO;
import com.ttn.ecommerce.model.SellerResponseDTO;
import com.ttn.ecommerce.repository.CustomerRepository;
import com.ttn.ecommerce.repository.SellerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequestMapping(path = "api/admin")
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmailService emailService;

    public List<CustomerResponseDTO> listAllCustomers(){
        List<Customer> customerList = customerRepository.findAll();
        List<CustomerResponseDTO> requiredCustomers = new ArrayList<>();

        // null or empty list conditions
        // paginate

        for (Customer customer : customerList) {
            User user = customer.getUser();
            CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
            customerResponseDTO.setUserId(user.getId());
            customerResponseDTO.setEmail(user.getEmail());
            String fullName = user.getFirstName()+" "+user.getMiddleName()+ " "+user.getLastName();
            customerResponseDTO.setFullName(fullName);
            customerResponseDTO.setActive(user.isActive());
            requiredCustomers.add(customerResponseDTO);
        }

        return requiredCustomers;
    }


    public List<SellerResponseDTO> listAllSellers(){
        List<Seller> sellerList = sellerRepository.findAll();
        List<SellerResponseDTO> requiredCustomers = new ArrayList<>();

        for (Seller seller : sellerList) {
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
            requiredCustomers.add(sellerResponseDTO);
        }

        return requiredCustomers;
    }


    public String activateUser(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            if(user.get().isActive()){
                return "User is already active.";
            }
            else {
                user.get().setActive(true);
                userRepository.save(user.get());
                emailService.sendIsActivatedMail(user.get());
                return "Account activated.";
            }
        }
        else{
            // add exception in handler
            throw new UserNotFoundException("User not found.");
        }

    }

    public String deActivateUser(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.get()!=null){
            if(user.get().isActive()){
                user.get().setActive(false);
                userRepository.save(user.get());
                emailService.sendDeActivatedMail(user.get());
                return "Account is deactivated.";
            }
            else {

                return "Account is already de-activated.";
            }
        }
        else{
            // add exception in handler
            throw new UserNotFoundException("User not found.");
        }

    }

}
