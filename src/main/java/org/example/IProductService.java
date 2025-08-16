package org.example;

import java.util.List;

public interface IProductService {
    void addProduct(Product product);
    void updateProduct(Product product);
    List<Product> getProducts();
    Product getProductById(int productId);
    void deleteProduct(int productId);
}
