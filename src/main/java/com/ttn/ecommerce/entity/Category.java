package com.ttn.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ttn.ecommerce.utils.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "category_gen")
    @SequenceGenerator(name="category_gen", sequenceName = "category_seq", initialValue = 1, allocationSize = 1)
    private long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Category> children = new HashSet<>();

}
