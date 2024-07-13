package com.misha.tastyfast.requests;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishesRequestForRestaurant {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private BigDecimal calories;
    private boolean inStock;


}