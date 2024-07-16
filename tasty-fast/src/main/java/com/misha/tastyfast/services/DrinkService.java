package com.misha.tastyfast.services;

import com.misha.tastyfast.comon.DrinkSpecification;
import com.misha.tastyfast.mapping.DrinkMapper;
import com.misha.tastyfast.model.Drink;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.repositories.DrinkRepository;
import com.misha.tastyfast.requests.drinkRequests.DrinkRequest;
import com.misha.tastyfast.requests.drinkRequests.DrinksResponse;
import com.misha.tastyfast.requests.pageResponse.PageResponse;
import com.misha.tastyfast.transactionHistory.DrinksTransactionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final DrinksTransactionHistoryRepository drinksTransactionHistoryRepository;
    private final DrinkMapper drinkMapper;
    private final FileStorageService fileStorageService;

    public Integer save(DrinkRequest drinkRequest, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Drink drink = drinkMapper.toDrink(drinkRequest);
        drink.setOwner(user);
        return drinkRepository.save(drink).getId();
    }

    public DrinksResponse findDrinkById(Integer drinkId, Authentication authentication){
        User user = ((User) authentication.getPrincipal());

        return drinkRepository.findById(drinkId)
                .map(drinkMapper::toDrinkResponse)
                .orElseThrow(() -> new EntityNotFoundException("No drink with provided restaurantId: " + drinkId));
    }


    public PageResponse <DrinksResponse> findAllDrinks(int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Integer id = user.getId();
        System.out.println("Here: " + id);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Drink> drinks = drinkRepository.findAllDisplayableDrinks(pageable, user.getId());
        List<DrinksResponse> drinksResponses = drinks.map(drinkMapper::toDrinkResponse).toList();
        System.out.println("Drinks retrieved:  " + drinksResponses.size());
        drinksResponses.forEach(drink -> System.out.println("Drink description: " + drink.getDrinkDescription() + "Id" + drink.getId()));
        return new PageResponse<>(
          drinksResponses,
          drinks.getNumber(),
          drinks.getSize(),
          drinks.getTotalElements(),
          drinks.getTotalPages(),
          drinks.isFirst(),
          drinks.isLast()
        );
    }


    public PageResponse<DrinksResponse> findAllDrinkByOwner (int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable= PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Drink> drinks = drinkRepository.findAll(DrinkSpecification.withOwnerId(user.getId()), pageable);
        List<DrinksResponse> drinksResponses = drinks.stream().map(drinkMapper::toDrinkResponse).toList();
        return new PageResponse<>();
    }




}
