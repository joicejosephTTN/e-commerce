package com.ttn.ecommerce.entity;

import com.ttn.ecommerce.utils.Auditable;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CategoryMetadataField extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metadata_gen")
    @SequenceGenerator(name="metadata_gen", sequenceName = "metadata_seq", initialValue = 1, allocationSize = 1)
    private long id;

    private String name;
}
