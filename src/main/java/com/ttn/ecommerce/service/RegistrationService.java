package com.ttn.ecommerce.service;
import com.ttn.ecommerce.advice.PasswordDoNotMatchException;
import com.ttn.ecommerce.advice.UserAlreadyExistsException;
import com.ttn.ecommerce.entity.*;
import com.ttn.ecommerce.model.CustomerDTO;
import com.ttn.ecommerce.model.SellerDTO;
import com.ttn.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RegistrationService {

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


    public Seller createSeller(SellerDTO sellerDTO){
        User user = new User();
        user.setFirstName(sellerDTO.getFirstName());
        user.setLastName(sellerDTO.getLastName());
        //optional
        user.setMiddleName(sellerDTO.getMiddleName());


        // checking if username(email) already exists
        String providedEmail = sellerDTO.getEmail();
        User existingUser = userRepository.findUserByEmail(providedEmail);
        if(existingUser!=null){
            throw new UserAlreadyExistsException("User with the provided email already exists.");
        }
        user.setEmail(providedEmail);

        // checking if password and reEnterPassword match
        if( !(sellerDTO.getPassword().equals(sellerDTO.getReEnterPassword())) ){
            throw new PasswordDoNotMatchException("Passwords do not match");
        }
        user.setPassword(bCryptPasswordEncoder.encode(sellerDTO.getReEnterPassword()));

        Role role = roleRepository.findRoleByAuthority("SELLER");
        user.setRole(role);

        Seller seller = new Seller();
        seller.setGst(sellerDTO.getGst());
        seller.setCompanyName(sellerDTO.getCompanyName());
        seller.setCompanyContact(sellerDTO.getCompanyContact());

        seller.setUser(user);

        Address address= new Address();
        address.setCity(sellerDTO.getAddress().getCity());
        address.setState(sellerDTO.getAddress().getState());
        address.setAddressLine(sellerDTO.getAddress().getAddressLine());
        address.setZipCode(sellerDTO.getAddress().getZipCode());
        address.setCountry(sellerDTO.getAddress().getCountry());
        address.setLabel(sellerDTO.getAddress().getLabel());

        address.setSeller(seller);

        addressRepository.save(address);
        sellerRepository.save(seller);
        userRepository.save(user);

        return seller;
    }


    public Customer createCustomer(CustomerDTO customerDTO){
        User user = new User();
        user.setFirstName(customerDTO.getFirstName());
        user.setLastName(customerDTO.getLastName());
        //optional
        user.setMiddleName(customerDTO.getMiddleName());
        // if username is unique
        user.setEmail(customerDTO.getEmail());
        //if password = reEnterPassword
        user.setPassword(bCryptPasswordEncoder.encode(customerDTO.getReEnterPassword()));

        Role role = roleRepository.findRoleByAuthority("CUSTOMER");
        user.setRole(role);

        Customer customer = new Customer();
        customer.setContact(customerDTO.getContact());
        customer.setUser(user);
        // multiple address -- might require separate logic

        customerDTO.getAddress().forEach(addressDTO -> {
            Address address= new Address();
            address.setCity(addressDTO.getCity());
            address.setState(addressDTO.getState());
            address.setAddressLine(addressDTO.getAddressLine());
            address.setZipCode(addressDTO.getZipCode());
            address.setCountry(addressDTO.getCountry());
            address.setLabel(addressDTO.getLabel());
            address.setCustomer(customer);

            addressRepository.save(address);

        });

        customerRepository.save(customer);
        userRepository.save(user);

        return customer;
    }

    //Testing purpose
    public List<User> returnAllUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }
}
