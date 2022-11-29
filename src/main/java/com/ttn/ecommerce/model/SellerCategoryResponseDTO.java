package com.ttn.ecommerce.model;

import com.ttn.ecommerce.entity.Category;
import lombok.Data;

import java.util.List;

@Data
public class SellerCategoryResponseDTO {
   private Long id;
   private String name;
   private Category parent;
   private List<SellerMetaResponseDTO> metadata;
}
