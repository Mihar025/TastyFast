package com.misha.tastyfast.requests;

import java.math.BigDecimal;

public record DrinkRequestForRestaurant(
        String  drinkName,

        String drinkDescription,

        double price,
        BigDecimal calories,
        String category,
        boolean inStock,
        boolean isAlcohol
) {


}
