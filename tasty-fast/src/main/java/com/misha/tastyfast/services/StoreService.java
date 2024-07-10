package com.misha.tastyfast.services;

import com.misha.tastyfast.exception.InternalServerErrorException;
import com.misha.tastyfast.mapping.DrinkMapper;
import com.misha.tastyfast.mapping.ProductMapper;
import com.misha.tastyfast.mapping.StoreMapper;
import com.misha.tastyfast.model.*;
import com.misha.tastyfast.repositories.DrinkRepository;
import com.misha.tastyfast.repositories.ProductRepository;
import com.misha.tastyfast.repositories.StoreRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    //Create new store
    //Find restaurant by id;
    // Update restaurant
    // delete restaurant
    // create new dish inside this restaurant
    // create new drink inside this restaurant
    // find all dishes inside restaurant
    // find all drinks inside restaurant
    // everything have to be authenticated
    // change dish photo
    // change drink photo

    private final StoreRepository storeRepository;
    private final StoreTransactionHistoryRepository storeTransactionHistoryRepository;
    private final StoreMapper storeMapper;
    private final ProductRepository productRepository;
    private final DrinkRepository drinkRepository;
    private final ProductMapper productMapper;
    private final DrinkMapper drinkMapper;
    private final DrinksTransactionHistoryRepository drinksTransactionHistoryRepository;
    private final ProductTransactionHistoryRepository productTransactionHistoryRepository;



    public Store createNewStore(StoreRequest request, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Store store = storeMapper.toStore(request, user);
        store.setOwner(user);
        store.setActive(true);
        Store savedStore = storeRepository.save(store);
        StoreTransactionHistory storeTransactionHistory = StoreTransactionHistory
                .builder()
                .store(savedStore)
                .user(user)
                .transactionType("CREATED")
                .transactionDate(LocalDateTime.now())
                .details("Restaurant was successfully created!")
                .build();
        storeTransactionHistoryRepository.save(storeTransactionHistory);
        return savedStore;
    }
    @Cacheable(value = "store:store:byId:", key ="#storeId + '_' + #connectedUser")

    public StoreResponse findStoreById (Integer storeId, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        return storeRepository.findById(storeId)
                .map(storeMapper::toStoreResponse)
                .orElseThrow(() -> new EntityNotFoundException("Store with provided ID::" + storeId + " wasn't founded"));
    }
    @Cacheable(value = "store:allProducts" , key = "#storeId")
    public PageResponse<ProductResponse> findAllProductInStore(int page, int size, Integer storeId) {
        log.info("Fetching products for stores with ID: {}", storeId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productRepository.findAllProductsInStore(pageable, storeId);

        List<ProductResponse> productResponses = products.getContent().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        log.info("Found {} dishes for restaurant with ID: {}", productResponses.size(), storeId);

        return new PageResponse<>(
                productResponses,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    @Cacheable(value = "store:allDrinks", key = "#storeId")
    public PageResponse<DrinksResponse> findAllDrinksInStore(int page, int size, Integer storeId) {
        log.info("Fetching drinks for store with ID: {}", storeId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Drink> drinks = drinkRepository.findAllDrinksInStore(pageable, storeId);
        List<DrinksResponse> responses = drinks.getContent().stream()
                .map(drinkMapper::toDrinkResponse)
                .collect(Collectors.toList());
        log.info("Found {} drinks for store with ID: {}", responses.size(), storeId);
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
    @Cacheable(value = "store:drink:byId", key = "#drinkId + '_' + #storeId")
    public DrinksResponse findDrinkByIdInStore(Integer drinkId, Integer storeId) {

        Drink drink = drinkRepository.findByIdAndStoreId(drinkId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find drink with id: " + drinkId));
        return drinkMapper.toDrinkResponse(drink);

    }

    @Transactional(readOnly = true)
    @Cacheable(value = "store:drink:byId", key = "#productId + '_' + #storeId")
    public ProductResponse findProductByIdInStore(Integer productId, Integer storeId) {

        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find dish with provided id: " + productId));
        return productMapper.toProductResponse(product);
    }

    @Cacheable(value = "store:allStores")
    public PageResponse<StoreResponse> findAllStores(int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        var owId = user.getId();
        System.out.println("Owner id: " + owId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page <Store> store = storeRepository.findAllDisplayedStores(pageable, user.getId());
        List<StoreResponse> storeResponses = store.map(storeMapper::toStoreResponse).stream().toList();
        System.out.println("Stores had been received: " + storeResponses.size());
        return new PageResponse<>(
                storeResponses,
                store.getNumber(),
                store.getSize(),
                store.getTotalElements(),
                store.getTotalPages(),
                store.isFirst(),
                store.isLast()
        );
    }

    @Cacheable(value = "store:allStoresWithoutDelivery")
    public PageResponse<StoreResponse> findAllStoresWithoutDelivery(int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        var Id = user.getId();
        System.out.println("User is here:" +Id);

        Pageable pageable = PageRequest.of(page, size , Sort.by("createdDate").descending());
        Page<Store> stores = storeRepository.findAllDisplayedStoresWithoutDelivery(pageable, user.getId());
        List<StoreResponse> responses = stores.map(storeMapper::toStoreResponse).stream().toList();
        System.out.println("Stores without delivery had been received" + responses.size());

        return new PageResponse<>(
                responses,
                stores.getNumber(),
                stores.getSize(),
                stores.getTotalElements(),
                stores.getTotalPages(),
                stores.isFirst(),
                stores.isLast()
        );
    }

    @CachePut(value = "store:update", key = "#id")
    public Store updateStore(Integer storeId, StoreRequest updateRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Store existingStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find store with provided id: " + storeId));

        if (!existingStore.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to update this store");
        }
        existingStore.setName(updateRequest.getStoreName());
        existingStore.setAddress(updateRequest.getAddress());
        existingStore.setPhoneNumber(updateRequest.getPhoneNumber());
        existingStore.setEmail(updateRequest.getEmail());
        existingStore.setDescription(updateRequest.getDescription());
        existingStore.setOpeningHours(updateRequest.getOpeningHours());
        existingStore.setDeliveryAvailable(updateRequest.isDeliveryAvailable());
        existingStore.setWebsiteUrl(updateRequest.getWebsiteUrl());
        existingStore.setLogoUrl(updateRequest.getLogoUrl());

        Store updatedStore = storeRepository.save(existingStore);
        StoreTransactionHistory transactionHistory = StoreTransactionHistory.builder()
                .store(updatedStore)
                .user(user)
                .transactionType("UPDATE")
                .transactionDate(LocalDateTime.now())
                .details("Restaurant updated")
                .build();
        storeTransactionHistoryRepository.save(transactionHistory);
        return existingStore;
    }

    @Transactional
    @CacheEvict(value = "store:deleteStore", key = "#id")
    public void deleteStore(Integer id,  Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Store existedStore = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find store with provided id:" + id))     ;
        if(!existedStore.getOwner().getId().equals(user.getId())){
            throw new AccessDeniedException("You don't have permission to update this restaurant");
        }

        StoreTransactionHistory transactionHistory = StoreTransactionHistory
                .builder()
                .store(existedStore)
                .user(user)
                .transactionType("DELETE")
                .transactionDate(LocalDateTime.now())
                .details("Restaurant deleting")
                .build();
        storeRepository.deleteById(id);
    }

    @Transactional
    @CachePut(value = "store:allProducts", key = "#storeId")
    public Product addProductToStore (Integer storeId, ProductRequestForStore request, Authentication authentication){
        User user = ((User) authentication.getPrincipal());
        Store existedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find store with provided id:" + storeId));
        if(!existedStore.getOwner().getId().equals(user.getId())){
            throw new AccessDeniedException("You don't have permission to add product  this restaurant");
        }
        Product product  = new Product();
        product.setProductName(request.getName());
        product.setProductDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCalories(request.getCalories());
        ProductTransactionHistory productTransactionHistory
                =
                ProductTransactionHistory
                        .builder()
                        .product(product)
                        .user(user)
                        .store(existedStore)
                        .transactionDate(LocalDateTime.now())
                        .transactionType("CREATE")
                        .details("Created new Dish")
                        .build();

        Product product1 = productRepository.save(product);

        existedStore.getProducts().add(product1);
        storeRepository.save(existedStore);
        return product1;
    }

    @Transactional
    @CachePut(value = "store:allDrinks", key = "#storeId")
    public Drink addDrinkToTheStore(Integer storeId, DrinkRequestForStore request, Authentication authentication){
        User user = ((User) authentication.getPrincipal());
        Store existedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new
                        EntityNotFoundException(
                        "Cannot find store with provided id:" + storeId));
        if(!existedStore.getOwner().getId().equals(user.getId())){
            throw new AccessDeniedException("You don't have permission to add this drink in the store");
        }
        Drink drink = new Drink();
        drink.setDrinksName(request.drinkName());
        drink.setDrinksDescription(request.drinkDescription());
        drink.setPrice(request.price());
        drink.setCategory(request.drinkCategory());
        drink.setCalories(request.calories());
        drink.setInStock(request.inStock());

        DrinksTransactionHistory drinksTransactionHistory = DrinksTransactionHistory.builder()
                .drink(drink)
                .user(user)
                .store(existedStore)
                .transactionDate(LocalDateTime.now())
                .transactionType("CREATE")
                .details("Created new Drink")
                .build();
        Drink drink1 = drinkRepository.save(drink);
        existedStore.getDrinks().add(drink1);
        storeRepository.save(existedStore);
        return drink;
    }
    @Transactional
    @CachePut(value = "store:drinks:update", key = "#storeId + '_' + #drinkId")
    public Drink updateDrinkInStore(Integer storeId, Integer drinkId, DrinkRequestForStore request, Authentication connectedUser) throws BadRequestException, InternalServerErrorException {
        User user = ((User) connectedUser.getPrincipal());
        Store existedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Store with provided ID:" + storeId));

        if(existedStore.getOwner().getId().equals(user.getId())){
            log.warn("User {} does not have permission to update drinks in store {}", user.getId(), storeId);
            throw new AccessDeniedException("You dont have permission to update this drink in the restaurant");
        }

        Drink existedDrink = drinkRepository.findByIdAndStoreId(drinkId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find drink in store with provided id::" + drinkId));


        try{
            updatedDrinkProperties(existedDrink, request);
            Drink updatedDrink = drinkRepository.save(existedDrink);

            createTransactionHistory(updatedDrink, existedStore, user);
            log.warn("Drink with id {} in store {} successfully updated", drinkId, storeId);
            return updatedDrink;
        }
        catch (IllegalArgumentException e){
            log.error("Invalid data provided for drink update: {}", e.getMessage());
            throw new BadRequestException("Invalid data provided for drink update: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("An error occurred while updating the drink: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
        }

    }

    @CachePut(value = "store:product:update", key = "#storeId + '_' + #productId")
    public Product updateProductForStore(Integer storeId, Integer productId, ProductRequestForStore request, Authentication connectedUser) throws BadRequestException, InternalServerErrorException {
        User user = ((User) connectedUser.getPrincipal());
        Store existedStore = storeRepository.findById(storeId)
                .orElseThrow(() ->new EntityNotFoundException("Cannot find store with provided id: " + storeId));

        if(!existedStore.getOwner().getId().equals(user.getId())){
            log.warn("User {} does not have permission to update product in store {}", user.getId(), storeId);
            throw new AccessDeniedException("You don't have permission to update this product in the store");
        }
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with provided id: " + productId + " in store with id: "+ storeId));

        try{
            updateProductInStore(product, request);
            Product updatedProduct = productRepository.save(product);
            createTransactionHistoryForProduct(updatedProduct, existedStore, user);
            log.info("Product with id {} in restaurant {} successfully updated", productId, storeId);
            return updatedProduct;
        }
        catch (IllegalArgumentException e) {
            log.error("Invalid data provided for dish update: {}", e.getMessage());
            throw new BadRequestException("Invalid data provided for dish update: " + e.getMessage());
        }  catch (Exception e) {
            log.error("An error occurred while updating the drink: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
        }

    }
    @Transactional
    @CacheEvict(value = "store:deleteDrink", key = "#drinkId + '_' + #storeId")
    public void deleteDrinkInsideStore(Integer drinkId, Integer storeId, Authentication ConnectedUser) throws InternalServerErrorException {
        User user = ((User) ConnectedUser.getPrincipal());
        Store store = storeRepository.findById(storeId).orElseThrow();
        if(store.getOwner().getId().equals(user.getId())){
            log.warn("User {} does not have permission to delete drink in store {}", user.getId(), storeId);
            throw new AccessDeniedException("You don't have permission to delete this dish in the restaurant");
        }

        Drink drink = drinkRepository.findByIdAndStoreId(drinkId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find drink with provided id: " + storeId));

        try{
           drinkRepository.delete(drink);
           deleteDrinkTransactionHistory(drink, store, user);
           log.info("Drink with id {} had been successfully deleted from store {}", drinkId, storeId);
        }catch (Exception e){
            log.error("An error occurred while deleting the drink: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
        }
    }
    @Transactional
    @CacheEvict(value = "store:deleteProduct", key = "#productId + '_' + #storeId")
    public void deleteProductInsideStore(Integer productId, Integer storeId, Authentication ConnectedUser) throws InternalServerErrorException {
        User user = ((User) ConnectedUser.getPrincipal());
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find store with provided id: " + storeId));

        if(store.getOwner().getId().equals(user.getId())){
            log.warn("User {} does not have permission to delete product in store {}", user.getId(), storeId);
            throw new AccessDeniedException("You don't have permission to delete this product in the store");
        }

        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with provided id: " + storeId));

        try{
            productRepository.delete(product);

            deleteProductTransactionalHistory(product, store, user);
            log.info("Drink with id {} had been successfully deleted from store {}", productId, storeId);
        }catch (Exception e){
            log.error("An error occurred while deleting the drink: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An error occurred while updating the drink: " + e.getMessage());
        }
    }

    private void deleteProductTransactionalHistory(Product product, Store store, User user) {
        ProductTransactionHistory productTransactionHistory =
                ProductTransactionHistory.builder()
                        .product(product)
                        .store(store)
                        .user(user)
                        .transactionType("DELETE")
                        .transactionDate(LocalDateTime.now())
                        .details("Delete product")
                        .build();
        productTransactionHistoryRepository.save(productTransactionHistory);
    }


    private void updatedDrinkProperties(Drink existedDrink, DrinkRequestForStore request) {
        existedDrink.setDrinksName(request.drinkName());
        existedDrink.setDrinksDescription(request.drinkDescription());
        existedDrink.setPrice(request.price());
        existedDrink.setCalories(request.calories());
        existedDrink.setCategory(request.drinkCategory());
        existedDrink.setInStock(request.inStock());
        existedDrink.setAlcohol(request.isAlcohol());
    }

    private void updateProductInStore(Product product, ProductRequestForStore request) {
        product.setProductName(request.getName());
        product.setProductDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCalories(request.getCalories());
        product.setInStock(request.isInStock());
        product.setProductCover(Arrays.toString(request.getProductCover()));
    }

    private void createTransactionHistoryForProduct(Product product, Store store, User user) {
      ProductTransactionHistory productTransactionHistory =
              ProductTransactionHistory.builder()
                      .product(product)
                      .store(store)
                      .user(user)
                      .transactionType("UPDATE")
                      .transactionDate(LocalDateTime.now())
                      .details("Updating product")
                      .build();
      productTransactionHistoryRepository.save(productTransactionHistory);
    }

    private void createTransactionHistory(Drink updatedDrink, Store existedStore, User user) {
        DrinksTransactionHistory transactionHistory = DrinksTransactionHistory.builder()
                .drink(updatedDrink)
                .store(existedStore)
                .user(user)
                .transactionType("UPDATE")
                .transactionDate(LocalDateTime.now())
                .details("Updating drink")
                .build();
        drinksTransactionHistoryRepository.save(transactionHistory);
    }

    private void deleteDrinkTransactionHistory(Drink drink, Store store, User user) {
        DrinksTransactionHistory transactionHistory = DrinksTransactionHistory.builder()
                .drink(drink)
                .store(store)
                .user(user)
                .transactionType("DELETE")
                .transactionDate(LocalDateTime.now())
                .details("Delete drink")
                .build();
        drinksTransactionHistoryRepository.save(transactionHistory);
    }





}
