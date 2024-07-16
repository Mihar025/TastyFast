package com.misha.tastyfast.requests.cartRequests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemRequest {

    private String itemType;
    private Integer itemId;
    private int quantity;

}
