package com.misha.tastyfast.requests.cartRequests;

import com.misha.tastyfast.requests.cartRequests.CartItemRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartRequest {
    private List<CartItemRequest> items;

}
