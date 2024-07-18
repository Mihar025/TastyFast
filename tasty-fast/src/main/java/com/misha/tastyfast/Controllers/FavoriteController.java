package com.misha.tastyfast.Controllers;

import com.misha.tastyfast.requests.favoriteRequests.FavoriteItemResponse;
import com.misha.tastyfast.requests.favoriteRequests.FavoriteRequest;
import com.misha.tastyfast.requests.favoriteRequests.FavoriteResponse;
import com.misha.tastyfast.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<FavoriteItemResponse>> getUserFavorites(Authentication authentication) {
        List<FavoriteItemResponse> favorites = favoriteService.getUserFavorite(authentication);
        return ResponseEntity.ok(favorites);
    }
}
