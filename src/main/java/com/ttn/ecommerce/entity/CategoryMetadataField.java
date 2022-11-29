package com.ttn.ecommerce.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CategoryMetadataField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "metadata_gen")
    @SequenceGenerator(name="metadata_gen", sequenceName = "metadata_seq", initialValue = 1, allocationSize = 1)
    private long id;

    private String name;
}
