package com.misha.tastyfast.Controllers;

import com.misha.tastyfast.requests.DishesRequest;
import com.misha.tastyfast.requests.DishesResponse;
import com.misha.tastyfast.requests.PageResponse;
import com.misha.tastyfast.services.DishesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("dishes")
@RequiredArgsConstructor
public class DishesController {
    private final DishesService dishesService;

    @PostMapping
    public ResponseEntity<Integer> createDish(@RequestBody DishesRequest dishesRequest, Authentication authentication) {
        Integer dishId = dishesService.save(dishesRequest, authentication);
        return ResponseEntity.ok(dishId);
    }

    @GetMapping("/{dishId}")
    public ResponseEntity<DishesResponse> getDishById(@PathVariable Integer dishId, Authentication authentication) {
        DishesResponse dishesResponse = dishesService.findDishesById(dishId, authentication);
        return ResponseEntity.ok(dishesResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<DishesResponse>> getAllDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<DishesResponse> response = dishesService.findAllDishes(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-dishes")
    public ResponseEntity<PageResponse<DishesResponse>> getMyDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<DishesResponse> response = dishesService.findAllDishesByOwner(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{dishId}/cover")
    public ResponseEntity<Void> uploadDishCover(
            @PathVariable Integer dishId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        dishesService.uploadProductCoverPicture(file, authentication, dishId);
        return ResponseEntity.ok().build();
    }


}
