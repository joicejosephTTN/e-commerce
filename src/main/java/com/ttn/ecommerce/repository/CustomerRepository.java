package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByContact(String contact);
}