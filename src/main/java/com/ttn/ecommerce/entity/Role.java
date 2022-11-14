package com.ttn.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_gen")
    @SequenceGenerator(name="role_gen", sequenceName = "role_seq", initialValue = 1, allocationSize = 1)
    private int id;

    private String authority;

}
