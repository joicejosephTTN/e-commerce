package com.ttn.ecommerce.entity;

import com.ttn.ecommerce.utils.Auditable;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class ProductVariation extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "variation_gen")
    @SequenceGenerator(name="product_gen", sequenceName = "product_seq", initialValue = 1, allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private float price;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Object metadata;

    private String imageName;

    private boolean isAvailable;
}
