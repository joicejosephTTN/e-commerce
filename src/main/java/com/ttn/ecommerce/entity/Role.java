package com.ttn.ecommerce.entity;

import javax.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_gen")
    @SequenceGenerator(name="role_gen", sequenceName = "role_seq", initialValue = 1, allocationSize = 1)
    private int id;

    private String authority;
}
