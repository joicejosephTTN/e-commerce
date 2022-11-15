package com.ttn.ecommerce;
import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CustomerRepositoryTests {

    @Autowired
    CustomerRepository customerRepo;

    @Test
    @Order(1)
    public void saveCustomerTest(){

        // setting user details
        User user = new User();
        user.setFirstName("Joice");
        user.setMiddleName("V");
        user.setLastName("Joseph");
        user.setEmail("joicejoseph@gmail.com");
        user.setPassword("supersecret");


        // list of address - for multiple addresses
        List<Address> addresses = new ArrayList<>();

        Address address = new Address();
        address.setCity("New Delhi");
        address.setState("Delhi");
        address.setZipCode(110074);
        address.setAddressLine("101 Green Acre Homes");
        address.setCountry("India");
        address.setLabel("Home");

        Address altAddress = new Address();
        altAddress.setCity("New Delhi");
        altAddress.setState("Delhi");
        altAddress.setZipCode(110017);
        altAddress.setAddressLine("15 Aastha Apartments");
        altAddress.setCountry("India");
        altAddress.setLabel("Home");

        // adding addresses to the list
        addresses.add(address);
        addresses.add(altAddress);

        // setting customer details
        Customer customer = new Customer();
        customer.setContact("9643238808");


        customer.setUser(user);
        address.setCustomer(customer);
        altAddress.setCustomer(customer);
        customer.setAddress(addresses);

        customerRepo.save(customer);

        Assertions.assertThat(customer.getId()).isGreaterThan(0);
        Assertions.assertThat(customer.getAddress()).hasSize(2);

    }

    @Test
    @Order(2)
    public void getCustomerTest(){
       Customer customer = customerRepo.findByContact("9643238808").get();
        Assertions.assertThat(customer.getContact()).isEqualTo("9643238808");
    }

    @Test
    @Order(3)
    public void updateCustomerTest(){
        Customer customer = customerRepo.findByContact("9643238808").get();
        customer.setContact("0123456789");

        Customer customerUpdated = customerRepo.save(customer);

        Assertions.assertThat(customerUpdated.getContact()).isEqualTo("0123456789");

    }

    @Test
    @Order(4)
    public void deleteCustomerTest(){
        Customer customer = customerRepo.findByContact("0123456789").get();
        customerRepo.delete(customer);

        Optional<Customer> optionalCustomer = customerRepo.findByContact("0123456789");

        Customer dummyCustomer = null;

        if(optionalCustomer.isPresent()){
            dummyCustomer = optionalCustomer.get();
        }

        Assertions.assertThat(dummyCustomer).isNull();
    }
}
