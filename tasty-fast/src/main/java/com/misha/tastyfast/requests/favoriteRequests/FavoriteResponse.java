package com.misha.tastyfast.requests.favoriteRequests;

import com.misha.tastyfast.model.Favorite;
import lombok.Data;

import java.util.List;

public class FavoriteResponse {

    private List<Favorite> favoriteList;

    public FavoriteResponse(List<Favorite> favorites) {
    }
}
