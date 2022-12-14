package com.ttn.ecommerce.entity;

import com.ttn.ecommerce.utils.Auditable;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Product extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_gen")
    @SequenceGenerator(name="product_gen", sequenceName = "product_seq", initialValue = 1, allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String brand;

    private boolean isActive;

    private boolean isDeleted;

    private boolean isCancellable;

    private boolean isReturnable;

}
