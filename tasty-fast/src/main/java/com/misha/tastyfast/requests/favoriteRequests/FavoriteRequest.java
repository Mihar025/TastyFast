package com.misha.tastyfast.requests.favoriteRequests;

import com.misha.tastyfast.model.Favorite;
import lombok.Data;


@Data
public class FavoriteRequest {

    private Integer userId;
    private Integer productId;
    private Integer dishesId;
    private Integer drinkId;
    private Integer restaurantId;
    private Integer storeId;

}
