package com.misha.tastyfast.requests;

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
