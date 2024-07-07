package com.misha.tastyfast.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DrinkRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String  drinkName,
        @NotNull(message = "101")
        @NotEmpty(message = "101")
        String drinkDescription,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        double price,
        @NotNull(message = "103")
        @NotEmpty(message = "103")
        BigDecimal calories,

        boolean inStock
) {
}
