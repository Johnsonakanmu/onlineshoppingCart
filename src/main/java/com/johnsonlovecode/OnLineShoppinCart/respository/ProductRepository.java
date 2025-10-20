package com.johnsonlovecode.OnLineShoppinCart.respository;

import com.johnsonlovecode.OnLineShoppinCart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    public List<Product> findByIsActiveTrue();

    List<Product> findByCategory(String category);
}
