package com.misha.tastyfast.services;

import com.misha.tastyfast.exception.InternalServerErrorException;
import com.misha.tastyfast.mapping.DishesMapper;
import com.misha.tastyfast.mapping.DrinkMapper;
import com.misha.tastyfast.mapping.RestaurantMapper;
import com.misha.tastyfast.model.Dishes;
import com.misha.tastyfast.model.Drink;
import com.misha.tastyfast.model.Restaurant;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.repositories.DishesRepository;
import com.misha.tastyfast.repositories.DrinkRepository;
import com.misha.tastyfast.repositories.RestaurantRepository;
import com.misha.tastyfast.requests.*;
import com.misha.tastyfast.transactionHistory.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
private final RestaurantRepository restaurantRepository;
private final RestaurantTransactionHistoryRepository restaurantTransactionHistoryRepository;
private final RestaurantMapper restaurantMapper;
private final DishesRepository dishesRepository;
private final DrinkRepository drinkRepository;
private final DishesMapper dishesMapper;
private final DrinkMapper drinkMapper;
private final DrinksTransactionHistoryRepository drinksTransactionHistoryRepository;
private final DishesTransactionRepository dishesTransactionRepository;



    public Restaurant createNewRestaurant(RestaurantRequest request, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Restaurant restaurant = restaurantMapper.toRestaurant(request, user);
        restaurant.setOwner(user);
        restaurant.setActive(true);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        RestaurantTransactionHistory restaurantTransactionHistory = RestaurantTransactionHistory
                .builder()
                .restaurant(savedRestaurant)
                .user(user)
                .transactionType("CREATED")
                .transactionDate(LocalDateTime.now())
                .details("Restaurant was successfully created!")
                .build();
        restaurantTransactionHistoryRepository.save(restaurantTransactionHistory);

        return savedRestaurant;
    }
    @Cacheable(value = "restaurant:byId", key = "#restaurantId")
    public RestaurantResponse findRestaurantById (Integer restaurantId, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        return restaurantRepository.findById(restaurantId)
                .map(restaurantMapper::toRestaurantResponse)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant with provided ID::" + restaurantId + " wasn't founded"));
    }
    @Cacheable(value = "restaurant:allDish", key = "#restaurantId")
    public PageResponse<DishesResponse> findAllDishesInRestaurant(int page, int size, Integer restaurantId) {
        log.info("Fetching dishes for restaurant with ID: {}", restaurantId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Dishes> dishes = dishesRepository.findAllDishesInRestaurant(pageable, restaurantId);

        List<DishesResponse> dishesResponses = dishes.getContent().stream()
                .map(dishesMapper::toDishesResponse)
                .collect(Collectors.toList());

        log.info("Found {} dishes for restaurant with ID: {}", dishesResponses.size(), restaurantId);

        return new PageResponse<>(
                dishesResponses,
                dishes.getNumber(),
                dishes.getSize(),
                dishes.getTotalElements(),
                dishes.getTotalPages(),
                dishes.isFirst(),
                dishes.isLast()
        );
    }
    @Cacheable(value = "restaurant:allDrinks", key = "#restaurantId + '_' + #page + '_' + #size")
    public PageResponse<DrinksResponse> findAllDrinksInRestaurant(int page, int size, Integer restaurantId) {
        log.info("Fetching drinks for restaurant with ID: {}", restaurantId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Drink> drinks = drinkRepository.findAllDrinksInRestaurant(pageable, restaurantId);
        List<DrinksResponse> responses = drinks.getContent().stream()
                .map(drinkMapper::toDrinkResponse)
                .collect(Collectors.toList());
        log.info("Found {} drinks for restaurant with ID: {}", responses.size(), restaurantId);
        return new PageResponse<>(
                responses,
                drinks.getNumber(),
                drinks.getSize(),
                drinks.getTotalElements(),
                drinks.getTotalPages(),
                drinks.isFirst(),
                drinks.isLast()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "restaurant:drink:byId", key = "#restaurantId + '_' + #drinkId")
    public DrinksResponse findDrinkByIdInRestaurant(Integer drinkId, Integer restaurantId) {

        Drink drink = drinkRepository.findByIdAndRestaurantId(drinkId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find drink with id: " + drinkId));
        return drinkMapper.toDrinkResponse(drink);

    }

    @Transactional(readOnly = true)
    @Cacheable(value = "restaurant:dish:byId", key = "#restaurantId + '_' + #dishId")
    public DishesResponse findDishByIdInRestaurant(Integer dishId, Integer restaurantId) {

        Dishes dishes = dishesRepository.findByIdAndRestaurantId(dishId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find dish w ith provided id: " + dishId));
        return dishesMapper.toDishesResponse(dishes);

    }
    @Cacheable(value = "restaurant:allRestaurants", key = "#page + '_' + #size + '_' + #connectedUser")
    public PageResponse<RestaurantResponse> findAllRestaurants(int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        var owId = user.getId();
        System.out.println("Owner id: " + owId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page <Restaurant> restaurants = restaurantRepository.findAllDisplayedRestaurants(pageable, user.getId());
        List<RestaurantResponse> restaurantResponses = restaurants.map(restaurantMapper::toRestaurantResponse).stream().toList();
        System.out.println("Restaurants had been received: " + restaurantResponses.size());
        return new PageResponse<>(
          restaurantResponses,
          restaurants.getNumber(),
          restaurants.getSize(),
          restaurants.getTotalElements(),
          restaurants.getTotalPages(),
          restaurants.isFirst(),
          restaurants.isLast()
        );
    }
    @Cacheable(value = "restaurant:allRestaurantsNoDelivery", key = "#page + '_' + #size + '_' + #connectedUser")
    public PageResponse<RestaurantResponse> findAllRestaurantsWithoutDelivery(int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        var Id = user.getId();
        System.out.println("User is here:" +Id);

        Pageable pageable = PageRequest.of(page, size , Sort.by("createdDate").descending());
        Page<Restaurant> restaurants = restaurantRepository.findAllDisplayedRestaurantsWithoutDelivery(pageable, user.getId());
        List<RestaurantResponse> responses = restaurants.map(restaurantMapper::toRestaurantResponse).stream().toList();
        System.out.println("Restaurants without delivery had been received" + responses.size());

        return new PageResponse<>(
                responses,
                restaurants.getNumber(),
                restaurants.getSize(),
                restaurants.getTotalElements(),
                restaurants.getTotalPages(),
                restaurants.isFirst(),
                restaurants.isLast()
        );
    }
    @Cacheable(value = "restaurant:drinks:update", key = "#id + '_' + #updateRequest + '_' + #authentication")
    public Restaurant updateRestaurant(Integer id, RestaurantRequest updateRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find restaurant with provided id: " + id));

        if (!existingRestaurant.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to update this restaurant");
        }
        existingRestaurant.setName(updateRequest.getRestaurantName());
        existingRestaurant.setAddress(updateRequest.getAddress());
        existingRestaurant.setPhoneNumber(updateRequest.getPhoneNumber());
        existingRestaurant.setEmail(updateRequest.getEmail());
        existingRestaurant.setDescription(updateRequest.getDescription());
        existingRestaurant.setOpeningHours(updateRequest.getOpeningHours());
        existingRestaurant.setCuisineType(updateRequest.getCuisineType());
        existingRestaurant.setSeatingCapacity(updateRequest.getSeatingCapacity());
        existingRestaurant.setDeliveryAvailable(updateRequest.isDeliveryAvailable());
        existingRestaurant.setWebsiteUrl(updateRequest.getWebsiteUrl());
         existingRestaurant.setLogoUrl(updateRequest.getLogoUrl());
        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        RestaurantTransactionHistory transactionHistory = RestaurantTransactionHistory.builder()
                .restaurant(updatedRestaurant)
                .user(user)
                .transactionType("UPDATE")
                .transactionDate(LocalDateTime.now())
                .details("Restaurant updated")
                .build();
        restaurantTransactionHistoryRepository.save(transactionHistory);

        return updatedRestaurant;
    }
    @Transactional
    @CacheEvict(value = "restaurant:delete" , key = "#id + '_' + #connectedUser")
    public void deleteRestaurant(Integer id,  Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Restaurant existingRest = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find restaurant with provided id:" + id))     ;
        if(!existingRest.getOwner().getId().equals(user.getId())){
            throw new AccessDeniedException("You don't have permission to delete the restaurant");
        }
        RestaurantTransactionHistory transactionHistory = RestaurantTransactionHistory
                .builder()
                .restaurant(existingRest)
                .user(user)
                .transactionType("DELETE")
                .transactionDate(LocalDateTime.now())
                .details("Restaurant deleting")
                .build();
        restaurantTransactionHistoryRepository.save(transactionHistory);
        restaurantRepository.deleteById(id);
    }

    @Transactional
    @Cacheable(value = "restaurant:allDish", key = "#restaurantId + '_' + #request + '_' + #authentication")
    public Dishes addDishesToRestaurant (Integer restaurantId, DishesRequestForRestaurant request, Authentication authentication){
        User user = ((User) authentication.getPrincipal());
        Restaurant existingRest = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find restaurant with provided id:" + restaurantId))     ;
        if(!existingRest.getOwner().getId().equals(user.getId())){
            throw new AccessDeniedException("You don't have permission to add dishes to this restaurant");
        }
        Dishes dishes = new Dishes();
        dishes.setDishesName(request.getName());
        dishes.setDishesDescription(request.getDescription());
        dishes.setPrice(request.getPrice());
        dishes.setCalories(request.getCalories());
       // dishes.setVegetarian(request.isVegetarian());
        dishes.setRestaurant(existingRest);

        DishesTransactionHistory dishesTransactionHistory
                =
                DishesTransactionHistory
                .builder()
                .dishes(dishes)
                .user(user)
                .restaurant(existingRest)
                .transactionDate(LocalDateTime.now())
                .transactionType("CREATE")
                .details("Created new Dish")
                .build();
        dishesTransactionRepository.save(dishesTransactionHistory);
        Dishes dishes1 = dishesRepository.save(dishes);

        existingRest.getDishes().add(dishes1);
        restaurantRepository.save(existingRest);
        return dishes1;
    }
    @Transactional
    @Cacheable(value = "restaurant:allDrinks", key = "#restaurantId + '_' + #request + '_' + #authentication")
    public Drink addDrinkToTheRestaurant(Integer restaurantId, DrinkRequestForRestaurant request, Authentication authentication){
        User user = ((User) authentication.getPrincipal());
        Restaurant existingRest = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new
                        EntityNotFoundException(
                                "Cannot find restaurant with provided id:" + restaurantId));
        if(!existingRest.getOwner().getId().equals(user.getId())){
            throw new AccessDeniedException("You don't have permission to adding this drink in the store");
        }
        Drink drink = new Drink();
        drink.setDrinksName(request.drinkName());
        drink.setDrinksDescription(request.drinkDescription());
        drink.setPrice(request.price());
        drink.setCategory(request.category());
        drink.setCalories(request.calories());
        drink.setInStock(request.inStock());

        DrinksTransactionHistory drinksTransactionHistory = DrinksTransactionHistory.builder()
                .drink(drink)
                .user(user)
                .restaurant(existingRest)
                .transactionDate(LocalDateTime.now())
                .transactionType("CREATE")
                .details("Created new Drink")
                .build();
        drinksTransactionHistoryRepository.save(drinksTransactionHistory);
        Drink drink1 = drinkRepository.save(drink);
        existingRest.getDrinks().add(drink1);
        restaurantRepository.save(existingRest);

        return drink;
    }

    @Transactional
    @Cacheable(value = "restaurant:drinks:update", key = "#restaurantId + '_' + #drinkId + '_' + #request")
    public Drink updateDrinkInRestaurant(Integer restaurantId, Integer drinkId, DrinkRequestForRestaurant request, Authentication connectedUser) throws BadRequestException, InternalServerErrorException {
        User user = (User) connectedUser.getPrincipal();
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find restaurant with provided id: " + restaurantId));

        if (!existingRestaurant.getOwner().getId().equals(user.getId())) {
            log.warn("User {} does not have permission to update drinks in restaurant {}", user.getId(), restaurantId);
            throw new AccessDeniedException("You don't have permission to update this drink in the restaurant");
        }

        Drink existingDrink = drinkRepository.findByIdAndRestaurantId(drinkId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find drink in restaurant with provided id: " + drinkId));

        try {
            updateDrinkProperties(existingDrink, request);
            Drink updatedDrink = drinkRepository.save(existingDrink);

            createTransactionHistory(updatedDrink, existingRestaurant, user);

            log.info("Drink with id {} in restaurant {} successfully updated", drinkId, restaurantId);
            return updatedDrink;
        } catch (IllegalArgumentException e) {
            log.error("Invalid data provided for drink update: {}", e.getMessage());
            throw new BadRequestException("Invalid data provided for drink update: " + e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while updating the drink: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
        }
    }

    @Transactional
    @Cacheable(value = "restaurant:dish:update", key = "#restaurantId + '_' + #dishId + '_' + #request")
    public Dishes updateDishInRestaurant(Integer restaurantId, Integer dishId, DishesRequestForRestaurant request, Authentication connectedUser) throws BadRequestException, InternalServerErrorException {
        User user = (User) connectedUser.getPrincipal();
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find restaurant with provided id: " + restaurantId));

        if (!existingRestaurant.getOwner().getId().equals(user.getId())) {
            log.warn("User {} does not have permission to update dish in restaurant {}", user.getId(), restaurantId);
            throw new AccessDeniedException("You don't have permission to update this dish in the restaurant");
        }

        Dishes existingDish = dishesRepository.findByIdAndRestaurantId(dishId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find dish with id " + dishId + " in restaurant with id: " + restaurantId));

        try {
            updateDishProperties(existingDish, request);
            Dishes updatedDish = dishesRepository.save(existingDish);
            createTransactionHistoryForDishes(updatedDish, existingRestaurant, user);
            log.info("Dish with id {} in restaurant {} successfully updated", dishId, restaurantId);
            return updatedDish;
        } catch (IllegalArgumentException e) {
            log.error("Invalid data provided for dish update: {}", e.getMessage());
            throw new BadRequestException("Invalid data provided for dish update: " + e.getMessage());
        }  catch (Exception e) {
            log.error("An error occurred while updating the drink: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
        }
    }

    @Transactional
    @CacheEvict(value = "restaurant:delete", key = "#dishesId + '_' + restaurantId + '_' + #connectedUser")
    public void deleteDishInsideRestaurant(Integer dishesId, Integer restaurantId, Authentication connectedUser) throws InternalServerErrorException {
        User user = ((User) connectedUser.getPrincipal());
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find restaurant with provided id: " + restaurantId));
        if (!existingRestaurant.getOwner().getId().equals(user.getId())) {
            log.warn("User {} does not have permission to update dish in restaurant {}", user.getId(), restaurantId);
            throw new AccessDeniedException("You don't have permission to delete this dish in the restaurant");
        }
        Dishes deleteDish =  dishesRepository.findByIdAndRestaurantId(dishesId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find dish with provided id: " + dishesId));

          try{
              dishesRepository.delete(deleteDish);
              deletingTransactionHistoryForDishes(deleteDish, existingRestaurant, user);
              log.info("Dish with id {} successfully deleted from restaurant {}", dishesId, restaurantId);
          } catch (Exception e) {
              log.error("An error occurred while updating the drink: {}", e.getMessage(), e);
              throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
          }
    }


    private void updateDrinkProperties(Drink drink, DrinkRequestForRestaurant request) {
        drink.setDrinksName(request.drinkName());
        drink.setDrinksDescription(request.drinkDescription());
        drink.setPrice(request.price());
        drink.setCalories(request.calories());
        drink.setCategory(request.category());
        drink.setInStock(request.inStock());
        drink.setAlcohol(request.isAlcohol());
    }

    private void updateDishProperties(Dishes dishes, DishesRequestForRestaurant request){
        dishes.setDishesName(request.getName());
        dishes.setDishesDescription(request.getDescription());
        dishes.setPrice(request.getPrice());
        dishes.setCategory(request.getCategory());
        dishes.setCalories(request.getCalories());
        dishes.setInStock(request.isInStock());
        dishes.setDishesCover(request.getDishesCover());
    }

    private void createTransactionHistory(Drink drink, Restaurant restaurant, User user) {
        DrinksTransactionHistory transactionHistory = DrinksTransactionHistory.builder()
                .drink(drink)
                .restaurant(restaurant)
                .user(user)
                .transactionType("UPDATE")
                .transactionDate(LocalDateTime.now())
                .details("Updating drink")
                .build();
        drinksTransactionHistoryRepository.save(transactionHistory);
    }

    public void createTransactionHistoryForDishes(Dishes dishes, Restaurant restaurant, User user){
        DishesTransactionHistory dishesTransactionHistory = DishesTransactionHistory.builder()
                .dishes(dishes)
                .restaurant(restaurant)
                .user(user)
                .transactionType("UPDATE")
                .transactionDate(LocalDateTime.now())
                .details("Updating dish")
                .build();
        dishesTransactionRepository.save(dishesTransactionHistory);
    }

    private void deletingTransactionHistoryForDishes(Dishes dishes, Restaurant restaurant, User user){
        DishesTransactionHistory dishesTransactionHistory = DishesTransactionHistory.builder()
                .dishes(dishes)
                .restaurant(restaurant)
                .user(user)
                .transactionType("DELETE")
                .transactionDate(LocalDateTime.now())
                .details("Deleting dish from restaurant")
                .build();
        dishesTransactionRepository.save(dishesTransactionHistory);
    }

    private void deletingTransactionHistoryForDrinks(Drink drink, Restaurant restaurant, User user){
        DrinksTransactionHistory drinksTransactionHistory = DrinksTransactionHistory.builder()
                .drink(drink)
                .restaurant(restaurant)
                .user(user)
                .transactionType("DELETING")
                .transactionDate(LocalDateTime.now())
                .details("Deleting drink")
                .build();
        drinksTransactionHistoryRepository.save(drinksTransactionHistory);
    }























}
