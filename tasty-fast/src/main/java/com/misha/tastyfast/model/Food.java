package com.misha.tastyfast.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Food extends BaseEntity{

    private String foodName;

    private String restaurantName;
    private double price;

    private String FoodDescription;
    private String foodCover;

    private boolean inStock;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;









}
