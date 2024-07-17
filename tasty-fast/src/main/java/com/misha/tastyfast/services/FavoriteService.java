package com.misha.tastyfast.services;

import com.misha.tastyfast.model.*;
import com.misha.tastyfast.repositories.*;
import com.misha.tastyfast.requests.favoriteRequests.FavoriteRequest;
import com.misha.tastyfast.requests.favoriteRequests.FavoriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private  final DrinkRepository drinkRepository;
    private final UserRepository userRepository;
    private  final DishesRepository dishesRepository;

    public FavoriteResponse addToFavoriteList(FavoriteRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        if(request.getProductId() != null){
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            favorite.setProduct(product);
            favorite.setFavorite("PRODUCT");
        }
        else if(request.getDishesId() != null){
            Dishes dishes = dishesRepository.findById(request.getDishesId())
                    .orElseThrow(() -> new RuntimeException("Dishes not found"));
            favorite.setDishes(dishes);
            favorite.setFavorite("DISHES");
        }
        else if(request.getDrinkId() != null){
            Drink drink = drinkRepository.findById(request.getDrinkId())
                    .orElseThrow(() -> new RuntimeException("Drink not found"));
            favorite.setDrink(drink);
            favorite.setFavorite("DRINK");
        }
        else if(request.getRestaurantId() != null){
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            favorite.setRestaurant(restaurant);
            favorite.setFavorite("RESTAURANT");
        }
        else if(request.getStoreId() != null){
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Store not found"));
            favorite.setStore(store);
            favorite.setFavorite("STORE");
        }

        favoriteRepository.save(favorite);
        List<Favorite> favorites = favoriteRepository.findAllByUser(user);
        return new FavoriteResponse(favorites);
    }

}
