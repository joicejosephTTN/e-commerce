package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    List<User> findUserByEmailLike(String email);
}