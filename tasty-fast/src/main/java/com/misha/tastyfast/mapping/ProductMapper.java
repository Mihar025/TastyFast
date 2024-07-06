package com.misha.tastyfast.mapping;

import com.misha.tastyfast.model.Product;
import com.misha.tastyfast.requests.ProductRequest;
import com.misha.tastyfast.requests.ProductResponse;
import com.misha.tastyfast.services.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {


    public Product toProduct(ProductRequest request) {
        return Product.builder()
                .id(request.id())
                .productName(request.productName())
                .productDescription(request.productDescription())
                .price(request.price())
                .calories(request.calories())
                .inStock(request.inStock())
                .build();
    }


    public ProductResponse toProductResponse(Product product) {
            return  ProductResponse.builder()
                    .id(product.getId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .price(product.getPrice())
                    .calories(product.getCalories())
                    .owner(product.getOwner().fullName())
                    .inStock(product.isInStock())
                    .cover(FileUtils.readFileFromLocation(product.getProductCover()))
                    .rate(product.getRate())
                    .build();
    }


}
