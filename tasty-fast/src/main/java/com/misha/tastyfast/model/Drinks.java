package com.misha.tastyfast.model;


import com.misha.tastyfast.comon.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.math.BigDecimal;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_drinks")
@EntityListeners(AuditingEntityListener.class)
public class Drinks extends BaseEntity {

    private String drinkName;
    private BigDecimal calories;
    private double price;
    private String drinkDescription;
    private String drinkCover;
    private boolean inStock;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


}
