package com.ttn.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ttn.ecommerce.entity.Category;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
public class CategoryResponseDTO {
    private long id;
    private String name;
    private Category parent;
    private Set<Category> children = new HashSet<>();
}
