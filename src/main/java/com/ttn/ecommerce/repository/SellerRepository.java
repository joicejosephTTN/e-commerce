package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByGst(String gst);
}