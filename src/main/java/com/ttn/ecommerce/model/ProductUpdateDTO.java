package com.ttn.ecommerce.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductUpdateDTO {
    private String name;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private String description;

}
