package com.misha.tastyfast.ApplicationControllers;

import com.misha.tastyfast.requests.DrinkRequest;
import com.misha.tastyfast.requests.DrinksResponse;
import com.misha.tastyfast.requests.PageResponse;
import com.misha.tastyfast.services.DrinkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("drinks")
@Tag(name = "Drink")
@Slf4j
public class DrinkController {
    private final DrinkService  drinkService;

    @PostMapping
    public ResponseEntity<Integer> saveDrink(
            @Valid  @RequestBody DrinkRequest request,
            Authentication connectedUser
            ){
        return ResponseEntity.ok(drinkService.save(
        request, connectedUser));
    }

            @GetMapping
            public ResponseEntity<PageResponse<DrinksResponse>> findAllDrinks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(drinkService.findAllDrinks(page, size, connectedUser));
    }

    @GetMapping("/{drink-id}")
    public ResponseEntity <DrinksResponse> findDrinkById(@PathVariable("drink-id") Integer drinkId,
                                                                      Authentication connectedUser){
        return ResponseEntity.ok(drinkService.findDrinkById(drinkId, connectedUser));
    }


    @GetMapping("/owner")
    public ResponseEntity<PageResponse <DrinksResponse>> findAllDrinkByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return  ResponseEntity.ok(drinkService.findAllProductByOwner(page, size, connectedUser));
    }








}
