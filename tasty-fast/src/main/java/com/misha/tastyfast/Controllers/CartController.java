package com.misha.tastyfast.Controllers;

import com.misha.tastyfast.model.User;
import com.misha.tastyfast.requests.CartItemRequest;
import com.misha.tastyfast.requests.CartRequest;
import com.misha.tastyfast.requests.CartResponse;
import com.misha.tastyfast.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "cart" )
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        CartResponse cart = cartService.getCartForUser((User) authentication.getPrincipal());
        return ResponseEntity.ok(cart);
    }

    @PutMapping
    public ResponseEntity<CartResponse> updateCart(@RequestBody CartRequest cartRequest, Authentication authentication) {
        CartResponse updatedCart = cartService.updatedCart(cartRequest, authentication);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@RequestBody CartItemRequest itemRequest, Authentication authentication) {
        CartResponse updatedCart = cartService.addItemToCart(itemRequest, authentication);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Integer itemId, Authentication authentication) {
        CartResponse updatedCart = cartService.removeItemFromCart(itemId, authentication);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }


}
