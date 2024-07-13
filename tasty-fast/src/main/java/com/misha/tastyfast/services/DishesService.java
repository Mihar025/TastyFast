package com.misha.tastyfast.services;

import com.misha.tastyfast.comon.DishesSpecification;
import com.misha.tastyfast.mapping.DishesMapper;
import com.misha.tastyfast.model.Dishes;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.repositories.DishesRepository;
import com.misha.tastyfast.transactionHistory.DishesTransactionRepository;
import com.misha.tastyfast.requests.DishesRequest;
import com.misha.tastyfast.requests.DishesResponse;
import com.misha.tastyfast.requests.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishesService {

    private final DishesTransactionRepository dishesTransactionRepository;
    private final DishesRepository dishesRepository;
    private final DishesMapper dishesMapper;
    private final FileStorageService fileStorageService;

    public Integer save (DishesRequest dishesRequest, Authentication connectedUser){
        User user = ((User)  connectedUser.getPrincipal());
        Dishes dishes = dishesMapper.toDishes(dishesRequest);
        dishes.setOwner(user);
        return dishesRepository.save(dishes).getId();
    }


    public DishesResponse findDishesById(Integer dishesId, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        return dishesRepository.findById(dishesId)
                .map(dishesMapper::toDishesResponse)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find dish with provided restaurantId"));
    }

    public PageResponse<DishesResponse> findAllDishes(int page, int size, Authentication connectedUser){
        User user = ((User)connectedUser.getPrincipal());
        Integer id = user.getId();
        System.out.println("Current user is " + id);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Dishes> dishes = dishesRepository.findAllDisplayableDishes(pageable, user.getId());
        List<DishesResponse> dishesResponses = dishes.map(dishesMapper::toDishesResponse).stream().toList();
        System.out.println("Dishes retrieved:  " + dishesResponses.size());
        dishesResponses.forEach(dish -> System.out.println("Dishes description: " + dish.getDishesDescription()
        + dish.getId()));
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

    public PageResponse<DishesResponse> findAllDishesByOwner (int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate"));
        Page<Dishes>  dishes = dishesRepository.findAll(DishesSpecification.withOwnerId(user.getId()), pageable);
        List<DishesResponse> dishesResponses = dishes.stream().map(dishesMapper::toDishesResponse)
                .toList();
    return new PageResponse<>();
    }

    public void uploadProductCoverPicture(MultipartFile file, Authentication connectedUser, Integer productId){
        Dishes dishes = dishesRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("No product with ID:: " + productId ));

        User user = ((User)  connectedUser.getPrincipal());
        var dishesCover = fileStorageService.saveDishesFile(file, dishes, user.getId());
        dishes.setDishesCover(dishesCover);
        dishesRepository.save(dishes);
    }




}
