package com.ttn.ecommerce.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seller_gen")
    @SequenceGenerator(name="seller_gen", sequenceName = "seller_seq", initialValue = 1, allocationSize = 1)
    private long id;

    private String gst;

    private String companyContact;

    private String companyName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne(mappedBy = "seller",cascade = CascadeType.ALL)
    private Address address;

}
