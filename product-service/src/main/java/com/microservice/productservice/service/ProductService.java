package com.microservice.productservice.service;

import com.microservice.productservice.dto.ProductRequest;
import com.microservice.productservice.dto.ProductResponse;
import com.microservice.productservice.model.Product;
import com.microservice.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
       Product product = Product.builder()
                         .price(productRequest.getPrice())
                         .name(productRequest.getName())
                         .description(productRequest.getDescription())
                         .build();
       productRepository.save(product);
       log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
       return productRepository.findAll().stream().map(product -> mapToResponse(product)).toList();
    }

    public ProductResponse mapToResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
