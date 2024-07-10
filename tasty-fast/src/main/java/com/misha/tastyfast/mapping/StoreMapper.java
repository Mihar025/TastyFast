package com.misha.tastyfast.mapping;

import com.misha.tastyfast.model.Store;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.requests.StoreRequest;
import com.misha.tastyfast.requests.StoreResponse;
import org.springframework.stereotype.Service;

@Service
public class StoreMapper {

    public Store toStore(StoreRequest request, User user){
        return Store.builder()
                .name(request.getStoreName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .description(request.getDescription())
                .openingHours(request.getOpeningHours())
                .isActive(request.isActive())
                .deliveryAvailable(request.isDeliveryAvailable())
                .websiteUrl(request.getWebsiteUrl())
                .owner(user)
                .logoUrl(request.getLogoUrl())
                .build();
    }

    public StoreResponse toStoreResponse(Store store){
        return StoreResponse.builder()
                .id(store.getId())
                .storeName(store.getName())
                .address(store.getAddress())
                .phoneNumber(store.getPhoneNumber())
                .email(store.getEmail())
                .description(store.getDescription())
                .openingHours(store.getOpeningHours())
                .rating(store.getRating())
                .isActive(store.isActive())
                .deliveryAvailable(store.isDeliveryAvailable())
                .websiteUrl(store.getWebsiteUrl())
                .ownerId(store.getOwner().getId())
                .logoUrl(store.getLogoUrl())
                .build();


    }

}
