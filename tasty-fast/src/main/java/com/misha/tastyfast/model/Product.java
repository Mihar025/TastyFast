package com.misha.tastyfast.model;

import com.misha.tastyfast.comon.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_product")
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseEntity {

    private String productName;
    private String productDescription;
    private double price;
    private BigDecimal calories;
    private String productCover;
    private boolean inStock;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @ManyToOne
            @JoinColumn(name = "owner_id")
    private User owner;



}
