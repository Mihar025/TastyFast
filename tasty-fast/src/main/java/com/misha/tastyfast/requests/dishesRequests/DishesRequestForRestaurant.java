package com.misha.tastyfast.requests.dishesRequests;

import lombok.*;

import java.math.BigDecimal;
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