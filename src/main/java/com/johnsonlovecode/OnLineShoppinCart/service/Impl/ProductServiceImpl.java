package com.johnsonlovecode.OnLineShoppinCart.service.Impl;

import com.johnsonlovecode.OnLineShoppinCart.model.Product;
import com.johnsonlovecode.OnLineShoppinCart.respository.ProductRepository;
import com.johnsonlovecode.OnLineShoppinCart.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {

       return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {

        Product deleteProduct = productRepository.findById(id).orElse(null);

        if (deleteProduct != null){
            productRepository.delete(deleteProduct);
            return true;
        }

        return false;

    }

    @Override
    public Product getProductById(int id) {
       Product product = productRepository.findById(id).orElse(null);
       return  product;
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {
        Product oldProduct = getProductById(product.getId());
        if (oldProduct == null) {
            return null;
        }

        // Keep old image if no new image uploaded
        String imageName = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();

        oldProduct.setTitle(product.getTitle());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setImage(imageName);
        oldProduct.setDiscount(product.getDiscount());
        oldProduct.setIsActive(product.getIsActive());

        double discountPrice = product.getPrice() - (product.getPrice() * product.getDiscount() / 100.0);
        if (discountPrice < 0) discountPrice = 0.0;

        oldProduct.setDiscountPrice(discountPrice);


        // Save updated product
        Product updatedProduct = productRepository.save(oldProduct);

        // Save new image file if provided
        if (!image.isEmpty()) {
            try {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return updatedProduct;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;

        if (ObjectUtils.isEmpty(category)) {
            products = productRepository.findByIsActiveTrue();
        } else {
           products =  productRepository.findByCategory(category);
        }

        return products;
    }

}
