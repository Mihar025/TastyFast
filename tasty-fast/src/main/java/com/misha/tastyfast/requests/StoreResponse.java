package com.misha.tastyfast.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
    private Integer id;
    private String storeName;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String openingHours;
    private Double rating;
    private boolean isActive;
    private Boolean deliveryAvailable;
    private String websiteUrl;
    private Integer ownerId;
    private byte[] logoUrl;
}
