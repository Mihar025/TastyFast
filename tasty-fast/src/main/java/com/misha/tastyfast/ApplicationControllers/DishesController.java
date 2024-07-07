package com.misha.tastyfast.ApplicationControllers;

import com.misha.tastyfast.requests.*;
import com.misha.tastyfast.services.DishesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("dishes")
@Tag(name = "Dish")
@Slf4j
public class DishesController {

    private final DishesService dishesService;



    @PostMapping
    public ResponseEntity<Integer> saveDish(
            @Valid @RequestBody DishesRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(dishesService.save(
                request, connectedUser));
    }

    @GetMapping
    public ResponseEntity<PageResponse<DishesResponse>> findALllDishes(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(dishesService.findAllDishes(page, size, connectedUser));
    }

    @GetMapping("/{dish-id}")
    public ResponseEntity <DishesResponse> findDishById(@PathVariable("dish-id") Integer dishId,
                                                         Authentication connectedUser){
        return ResponseEntity.ok(dishesService.findDishesById(dishId, connectedUser));
    }


    @GetMapping("/owner")
    public ResponseEntity<PageResponse <DishesResponse>> findAllDishesByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return  ResponseEntity.ok(dishesService.findAllDishesByOwner(page, size, connectedUser));
    }


}
