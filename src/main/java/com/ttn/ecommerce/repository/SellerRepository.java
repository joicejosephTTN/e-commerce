package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findByGst(String gst);
    Seller findByCompanyNameIgnoreCase(String companyName);


}