package com.ttn.ecommerce.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
    @SequenceGenerator(name = "address_gen", sequenceName = "address_seq", initialValue = 1, allocationSize = 1 )
    private long id;

    private String city;

    private String state;

    private String country;

    private String addressLine;

    private long zipCode;

    private String label;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="seller_id")
    private Seller seller;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="customer_id")
    private Customer customer;

}