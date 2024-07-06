package com.misha.tastyfast.services;

import com.misha.tastyfast.comon.ProductSpecification;
import com.misha.tastyfast.exception.OperationNotPermittedException;
import com.misha.tastyfast.mapping.ProductMapper;
import com.misha.tastyfast.model.Product;
import com.misha.tastyfast.model.User;

import com.misha.tastyfast.repositories.ProducTransactionHistoryRepository;
import com.misha.tastyfast.repositories.ProductRepository;
import com.misha.tastyfast.repositories.ProductTransactionHistory;
import com.misha.tastyfast.requests.PageResponse;
import com.misha.tastyfast.requests.ProductRequest;
import com.misha.tastyfast.requests.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProducTransactionHistoryRepository producTransactionHistoryRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;


    public Integer save(ProductRequest request, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Product product = productMapper.toProduct(request);
        product.setOwner(user);
        return productRepository.save(product).getId();
    }

    public ProductResponse findProductById(Integer productId){
        return productRepository.findById(productId)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("No product founded with id: " + productId));
    }

    public PageResponse<ProductResponse> findAllProducts (int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Integer userId = user.getId();
        System.out.println("Current user is : " + userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products= productRepository.findAllDisplayableBooks(pageable, user.getId());
        List<ProductResponse> productResponses = products.map(productMapper::toProductResponse)
                .toList();
        System.out.println("Product retrieved: " + productResponses.size());
        productResponses.forEach(product -> System.out.println("Product description: " + product.getProductDescription() + "Id" + product.getId()));
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

    public PageResponse <ProductResponse>findAllProductsByOwner(int page, int size, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productRepository.findAll(ProductSpecification.withOwnerId(user.getId()), pageable);
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse)
                .toList();
        return new PageResponse<>();
    }

    public Integer makeOrder(Integer productId, Authentication connectedUser){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID::" + productId + " do not exist"));
        if(!product.isInStock()){
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(product.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot ordering twice the same order");
        }
        final boolean isAlreadyOrdered = producTransactionHistoryRepository.isAlreadyOrderedByUser(productId, user.getId());
        if(isAlreadyOrdered){
            throw new OperationNotPermittedException("You already made order");
        }

        ProductTransactionHistory productTransactionHistory = ProductTransactionHistory.builder()
                .user(user)
                .product(product)
                .returned(false)
                .alreadyOrdered(false)
                .build();
        return producTransactionHistoryRepository.save(productTransactionHistory).getId();

    }









    public void uploadProductCoverPicture(MultipartFile file, Authentication connectedUser, Integer productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("No product with ID:: " + productId ));

        User user = ((User)  connectedUser.getPrincipal());
        var productCover = fileStorageService.saveProductFile(file, product, user.getId());
        product.setProductCover(productCover);
        productRepository.save(product);

    }





}
