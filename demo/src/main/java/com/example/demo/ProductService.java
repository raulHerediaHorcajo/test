package com.example.demo;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();
    Optional<Product> findById(long id);
    Product addProduct(Product product);
    Product modifyProduct(long id, Product newProduct);
    void deleteProduct(long id);
}
