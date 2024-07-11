package com.misha.tastyfast.Controllers;

import com.misha.tastyfast.requests.DrinkRequest;
import com.misha.tastyfast.requests.DrinksResponse;
import com.misha.tastyfast.requests.PageResponse;
import com.misha.tastyfast.services.DrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("drink")
@RequiredArgsConstructor
public class DrinkController {

    private final DrinkService drinkService;

    @PostMapping
    public ResponseEntity<Integer> createDrink(@RequestBody DrinkRequest drinkRequest, Authentication authentication) {
        Integer drinkId = drinkService.save(drinkRequest, authentication);
        return ResponseEntity.ok(drinkId);
    }

    @GetMapping("/{drinkId}")
    public ResponseEntity<DrinksResponse> getDrinkById(@PathVariable Integer drinkId, Authentication authentication) {
        DrinksResponse drinkResponse = drinkService.findDrinkById(drinkId, authentication);
        return ResponseEntity.ok(drinkResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<DrinksResponse>> getAllDrinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<DrinksResponse> response = drinkService.findAllDrinks(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-drinks")
    public ResponseEntity<PageResponse<DrinksResponse>> getMyDrinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<DrinksResponse> response = drinkService.findAllDrinkByOwner(page, size, authentication);
        return ResponseEntity.ok(response);
    }


}
