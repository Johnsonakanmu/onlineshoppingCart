package com.johnsonlovecode.OnLineShoppinCart.service.Impl;

import com.johnsonlovecode.OnLineShoppinCart.model.Category;
import com.johnsonlovecode.OnLineShoppinCart.respository.CategoryRepository;
import com.johnsonlovecode.OnLineShoppinCart.service.CategoryService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    private ModelMapper modelMapper;


    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Boolean existCategory(String name) {
        // existByName ids coming for category Repository
        return categoryRepository.existsByName(name);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Boolean deleteCategory(int id) {

       Category category = categoryRepository.findById(id).orElse(null);

       if(category != null){
           categoryRepository.delete(category);
           return true;
       }

       return false;

    }

    @Override
    public Category getCategoryById(int id) {
       Category category = categoryRepository.findById(id).orElse(null);

       return category;
    }

    @Override
    public List<Category> getAllActiveCategory() {
       List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories;
    }
}
