package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class CategoryResponseDTO {
    private long id;
    private String name;
    private Category parent;
    private Set<ChildCategoryDTO> children = new HashSet<>();
    private List<MetadataResponseDTO> metadataList = new ArrayList<>();

}
