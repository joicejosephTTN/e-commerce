package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Category;
import lombok.Data;

@Data
public class CategoryDTO {

    private String name;

    private Long parentId;
}
