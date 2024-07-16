package com.misha.tastyfast.services;

import com.misha.tastyfast.mapping.CartMapper;
import com.misha.tastyfast.model.*;
import com.misha.tastyfast.repositories.CartRepository;
import com.misha.tastyfast.requests.cartRequests.CartItemRequest;
import com.misha.tastyfast.requests.cartRequests.CartItemResponse;
import com.misha.tastyfast.requests.cartRequests.CartRequest;
import com.misha.tastyfast.requests.cartRequests.CartResponse;
import com.misha.tastyfast.requests.dishesRequests.DishesResponse;
import com.misha.tastyfast.requests.drinkRequests.DrinksResponse;
import com.misha.tastyfast.requests.productRequests.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductService productService;
    private final DrinkService drinkService;
    private final DishesService dishesService;

    public CartResponse getCartForUser(User user){
        Cart cart = getOrCreateCart(user);
        return cartMapper.toCartResponse(cart);
    }


    public CartResponse updatedCart(CartRequest cartRequest, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        for (CartItemRequest itemRequest : cartRequest.getItems()) {
            addItemToCart(itemRequest, connectedUser);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(updatedCart);
    }


    public CartResponse removeItemFromCart(Integer itemId, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().removeIf(item -> item.getCartItem_id().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(updatedCart);
    }

    public CartResponse addItemToCart(CartItemRequest itemRequest, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Cart cart = getOrCreateCart(user);
        CartItem cartItem = findCartItem(cart, itemRequest.getItemType(), itemRequest.getItemId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setItemType(itemRequest.getItemType());
            cartItem.setItemId(itemRequest.getItemId());
            cartItem.setQuantity(itemRequest.getQuantity());
            setItemDetails(cartItem, itemRequest, connectedUser);
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + itemRequest.getQuantity());
        }
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(updatedCart);
    }

    public void clearCart(User user){
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public OrderItem createOrderItemFromCartItem(CartItemResponse cartItemResponse, Order order){
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItemName(cartItemResponse.getItemType());
        orderItem.setItemType(cartItemResponse.getItemType());
        orderItem.setId(cartItemResponse.getId());
        orderItem.setQuantity(cartItemResponse.getQuantity());
        orderItem.setPrice(cartItemResponse.getPrice());
        return orderItem;
    }







    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private Cart getOrCreateCart(User user){
        return user.getCart() != null ? user.getCart() : createCart(user);
    }


    private void setItemDetails(CartItem cartItem, CartItemRequest cartItemRequest, Authentication connectedUser) {
        switch(cartItemRequest.getItemType()) {
            case "PRODUCT":
                ProductResponse productResponse = productService.findProductById(cartItemRequest.getItemId(), connectedUser);
                cartItem.setItemName(productResponse.getProductName());
                cartItem.setPrice(productResponse.getPrice());
                break;
            case "DISH":
                DishesResponse dishesResponse = dishesService.findDishesById(cartItemRequest.getItemId(), connectedUser);
                cartItem.setItemName(dishesResponse.getDishesName());
                cartItem.setPrice(dishesResponse.getPrice());
                break;
            case "DRINK":
                DrinksResponse drinksResponse = drinkService.findDrinkById(cartItemRequest.getItemId(), connectedUser);
                cartItem.setItemName(drinksResponse.getDrinkName());
                cartItem.setPrice(drinksResponse.getPrice());
                break;
            default:
                throw new IllegalArgumentException("Invalid item type: " + cartItemRequest.getItemType());
        }
    }




   private CartItem findCartItem(Cart cart, String itemType, Integer itemId){
       return cart.getCartItems().stream()
               .filter(item -> item.getItemType().equals(itemType) && item.getItemId().equals(itemId))
               .findFirst()
                .orElse(null);
    }




}
