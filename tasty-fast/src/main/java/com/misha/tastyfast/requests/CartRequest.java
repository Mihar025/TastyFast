package com.misha.tastyfast.requests;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;

import java.util.List;

@Data
@Builder
public class CartRequest {
    private List<CartItemRequest> items;

}
