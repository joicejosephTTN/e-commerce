package com.ttn.ecommerce.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "variation_gen")
    @SequenceGenerator(name="product_gen", sequenceName = "product_seq", initialValue = 1, allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private float price;

    private String metadata;

    private String imageName;

    private boolean isAvailable;
}
