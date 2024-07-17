package com.misha.tastyfast.requests.drinkRequests;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrinksResponse {


    private Integer id;
    private String drinkName;
    private String drinkDescription;
    private BigDecimal price;
    private BigDecimal calories;
    private String owner;
    private boolean inStock;
    private double rate;
    private Integer quantity;

}
