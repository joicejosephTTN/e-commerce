package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findRoleByAuthority(String Role);
}