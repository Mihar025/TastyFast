package com.misha.tastyfast.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OrderItemRequest {
    private String itemType;
    private Integer itemId;
    private Integer quantity;

}
