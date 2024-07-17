package com.misha.tastyfast.Controllers;

import com.misha.tastyfast.requests.favoriteRequests.FavoriteRequest;
import com.misha.tastyfast.requests.favoriteRequests.FavoriteResponse;
import com.misha.tastyfast.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest request) {
        FavoriteResponse response = favoriteService.addToFavoriteList(request);
        return ResponseEntity.ok(response);
    }
}
