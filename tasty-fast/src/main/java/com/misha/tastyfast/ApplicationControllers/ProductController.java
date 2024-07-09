package com.misha.tastyfast.ApplicationControllers;

import com.misha.tastyfast.requests.*;
import com.misha.tastyfast.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
@Tag(name = "Product")
@Slf4j
public class ProductController {
     private final ProductService productService;

     @PostMapping
     public ResponseEntity<Integer> saveProduct(
             @Valid @RequestBody ProductRequest request,
             Authentication connectedUser
     ){
          return ResponseEntity.ok(productService.save(
                  request, connectedUser));
     }

     @GetMapping
     public ResponseEntity<PageResponse<ProductResponse>> findAllProducts(
             @RequestParam(name = "page", defaultValue = "0", required = false) int page,
             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
             Authentication connectedUser
     ) {
          return ResponseEntity.ok(productService.findAllProducts(page, size, connectedUser));
     }

     @GetMapping("/{product-id}")
     public ResponseEntity<ProductResponse> findProductById(@PathVariable("product-id") Integer drinkId,
                                                            Authentication connectedUser){
          return ResponseEntity.ok(productService.findProductById(drinkId, connectedUser));
     }


     @GetMapping("/owner")
     public ResponseEntity<PageResponse <ProductResponse>> findAllProductsOwner(
             @RequestParam(name = "page", defaultValue = "0", required = false) int page,
             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
             Authentication connectedUser
     ) {
          return  ResponseEntity.ok(productService.findAllProductsByOwner(page, size, connectedUser));
     }




}
