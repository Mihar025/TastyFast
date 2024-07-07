package com.misha.tastyfast.mapping;

import com.misha.tastyfast.model.Dishes;
import com.misha.tastyfast.requests.DishesRequest;
import com.misha.tastyfast.requests.DishesResponse;
import com.misha.tastyfast.services.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class DishesMapper {
    public Dishes toDishes(DishesRequest dishesRequest) {
        return Dishes.builder()
                .id(dishesRequest.id())
                .dishesName(dishesRequest.dishesName())
                .dishesDescription(dishesRequest.dishesDescription())
                .price(dishesRequest.price())
                .calories(dishesRequest.calories())
                .inStock(dishesRequest.inStock())
                .build();

    }


    public DishesResponse toDishesResponse(Dishes dishes) {
        return DishesResponse.builder()
                .id(dishes.getId())
                .dishesName(dishes.getDishesName())
                .dishesDescription(dishes.getDishesDescription())
                .price(dishes.getPrice())
                .calories(dishes.getCalories())
                .owner(dishes.getDishesOwner())
                .inStock(dishes.isInStock())
                .cover(FileUtils.readFileFromLocation(dishes.getDishesCover()))
                .rate(dishes.getRate())
                .build();
    }
}
