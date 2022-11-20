package com.ttn.ecommerce.entity;

import com.ttn.ecommerce.utils.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Customer extends Auditable<String>  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "customer_gen")
    @SequenceGenerator(name="customer_gen", sequenceName = "customer_seq", initialValue = 1, allocationSize = 1)
    private long id;

    private String contact;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Address> address = new ArrayList<>();
}
