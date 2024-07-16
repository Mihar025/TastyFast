package com.misha.tastyfast.requests.drinkRequests;

import java.math.BigDecimal;

public record DrinkRequestForStore(

    String  drinkName,

    String drinkDescription,
    BigDecimal price,
    String drinkCategory,
    BigDecimal calories,
    boolean inStock,
     boolean isAlcohol
) {
}
