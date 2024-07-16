package com.misha.tastyfast.requests.drinkRequests;

import java.math.BigDecimal;

public record DrinkRequestForRestaurant(
        String  drinkName,

        String drinkDescription,

        BigDecimal price,
        BigDecimal calories,
        String category,
        boolean inStock,
        boolean isAlcohol
) {


}
