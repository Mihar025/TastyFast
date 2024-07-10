package com.misha.tastyfast.requests;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantResponse {

    private Integer id;
    private String restaurantName;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String openingHours;
    private Double rating;
    private String cuisineType;
    private boolean isActive;
    private Integer seatingCapacity;
    private Boolean deliveryAvailable;
    private String websiteUrl;
    private Integer ownerId;
    private byte[] logoUrl;

}
