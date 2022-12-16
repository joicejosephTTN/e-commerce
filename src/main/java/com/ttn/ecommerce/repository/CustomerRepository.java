package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Customer;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByContact(String contact);

}