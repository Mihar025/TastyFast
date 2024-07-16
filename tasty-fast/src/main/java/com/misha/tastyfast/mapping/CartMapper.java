package com.misha.tastyfast.mapping;

import com.misha.tastyfast.model.Cart;
import com.misha.tastyfast.model.CartItem;
import com.misha.tastyfast.requests.cartRequests.CartItemResponse;
import com.misha.tastyfast.requests.cartRequests.CartResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class CartMapper {
    public CartResponse toCartResponse(Cart cart) {
        return  CartResponse.builder()
                .id(cart.getCart_id())
                .items(cart.getCartItems().stream().map(this::mapCartItemToResponse).collect(Collectors.toList()))
                .totalAmount(calculateTotalAmount(cart))
                .build();
    }

    private CartItemResponse mapCartItemToResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getCartItem_id())
                .price(cartItem.getPrice())
                .itemType(cartItem.getItemType())
                .itemId(cartItem.getItemId())
                .itemName(cartItem.getItemName())
                .quantity(cartItem.getQuantity())
                .build();
    }

    private BigDecimal calculateTotalAmount(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
