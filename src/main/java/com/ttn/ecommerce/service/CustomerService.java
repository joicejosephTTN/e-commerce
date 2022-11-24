package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.AddressNotFoundException;
import com.ttn.ecommerce.exception.PasswordDoNotMatchException;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.AddressUpdateDTO;
import com.ttn.ecommerce.model.CustomerUpdateDTO;
import com.ttn.ecommerce.model.CustomerViewDTO;
import com.ttn.ecommerce.repository.AddressRepository;
import com.ttn.ecommerce.repository.CustomerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

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

    public CustomerViewDTO fetchProfile(String email){
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();

        CustomerViewDTO customerViewDTO = new CustomerViewDTO();

        // converting seller to appropriate responseDTO
        customerViewDTO.setUserId(user.getId());
        customerViewDTO.setFirstName(user.getFirstName());
        customerViewDTO.setLastName(user.getLastName());
        customerViewDTO.setActive(user.isActive());
        customerViewDTO.setContact(customer.getContact());

        return customerViewDTO;
    }


    public String updateProfile(String email, CustomerUpdateDTO customerUpdateDTO) {
        // getting associated user and customer
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();

        // partial updates
        BeanUtils.copyProperties(customerUpdateDTO, user, FilterProperties.getNullPropertyNames(customerUpdateDTO));
        BeanUtils.copyProperties(customerUpdateDTO, customer, FilterProperties.getNullPropertyNames(customerUpdateDTO));

        // saving updates
        userRepository.save(user);
        customerRepository.save(customer);
        return "Updated Successfully";
    }

    public String updatePassword(String email, String password, String confirmPass){
        User user = userRepository.findUserByEmail(email);
        if(!password.equals(confirmPass)){
            throw new PasswordDoNotMatchException("Password do not match.");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendSuccessfulChangeMail(user);
        return "Password updated successfully.";
    }

    public List<Address> fetchAddress(String email){
        User user = userRepository.findUserByEmail(email);
        Customer customer = user.getCustomer();
        List<Address> address = customer.getAddress();

        return address;
    }

    public String addAddress(String email, AddressDTO addressDTO){
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

        // save the address
        addressRepository.save(address);

        return "Address added successfully";
    }


    public String deleteAddress(String email, Long addressId){
        // getting associated user, customer
        User user = userRepository.findUserByEmail(email);
        Customer loggedCustomer = user.getCustomer();
        // getting requested address
        Optional<Address> address = addressRepository.findById(addressId);
        Address reqdAddress = address.get();

        if(address.isPresent()){
            // check to see if address is associated with logged in customer
            if (reqdAddress.getCustomer() == loggedCustomer){
                addressRepository.delete(reqdAddress);
                return "Address deleted successfully.";
            }
            throw new AddressNotFoundException("Address not associated with logged in customer");

        }

        throw new AddressNotFoundException("Address not found.");
    }

    public String updateAddress(String email, Long addressId, AddressUpdateDTO addressDTO){
        // getting associated user, customer
        User user = userRepository.findUserByEmail(email);
        Customer loggedCustomer = user.getCustomer();

        // getting requested address
        Optional<Address> address = addressRepository.findById(addressId);
        Address reqdAddress = address.get();

        if(address.isPresent()){
            // check to see if address is associated with logged in customer
            if (reqdAddress.getCustomer() == loggedCustomer){
                //partial update of address - ignoring null properties received in request
                BeanUtils.copyProperties(addressDTO, reqdAddress, FilterProperties.getNullPropertyNames(addressDTO));

                // saving updates
                reqdAddress.setCustomer(loggedCustomer);
                addressRepository.save(reqdAddress);

                return "Address updated successfully.";
            }
            throw new AddressNotFoundException("Address not associated with logged in customer");

        }
        throw new AddressNotFoundException("Address not found.");

    }
}
