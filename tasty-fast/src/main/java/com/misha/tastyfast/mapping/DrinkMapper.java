package com.misha.tastyfast.mapping;

import com.misha.tastyfast.model.Drink;
import com.misha.tastyfast.requests.DrinkRequest;
import com.misha.tastyfast.requests.DrinksResponse;
import com.misha.tastyfast.services.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class DrinkMapper {


    public Drink toDrink (DrinkRequest request){
        return Drink.builder()
                .id(request.id())
                .drinksName(request.drinkName())
                .drinksDescription(request.drinkDescription())
                .price(request.price())
                .calories(request.calories())
                .inStock(request.inStock())
                .build();
    }

    public DrinksResponse toDrinkResponse (Drink request){
        return DrinksResponse.builder()
                .id(request.getId())
                .drinkName(request.getDrinksName())
                .drinkDescription(request.getDrinksDescription())
                .price(request.getPrice())
                .calories(request.getCalories())
                .owner(request.getDrinksOwner())
                .inStock(request.isInStock())
                .cover(FileUtils.readFileFromLocation(request.getDrinksCover()))
                .rate(request.getRate())
                .build();
    }


}
