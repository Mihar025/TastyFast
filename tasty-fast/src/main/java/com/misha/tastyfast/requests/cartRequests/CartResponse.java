package com.misha.tastyfast.requests.cartRequests;

import com.misha.tastyfast.requests.cartRequests.CartItemResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {

    private Integer id;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;

}
