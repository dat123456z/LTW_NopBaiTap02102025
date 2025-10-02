package com.example.demo.service.impl;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepo;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepo.findById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepo.deleteById(id);
    }
}
