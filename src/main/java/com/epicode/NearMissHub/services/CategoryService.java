package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import com.epicode.NearMissHub.entities.Category;
import com.epicode.NearMissHub.exceptions.BadRequestException;
import com.epicode.NearMissHub.payloads.request.CreateCategoryRequest;
import com.epicode.NearMissHub.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categories;

    public CategoryService(CategoryRepository categories) {
        this.categories = categories;
    }

    public Category create(CreateCategoryRequest body) {
        categories.findByName(body.name).ifPresent(c -> {
            throw new BadRequestException("Category already exists");
        });

        Category c = new Category();
        c.setId(UUID.randomUUID());
        c.setName(body.name);
        c.setDescription(body.description);
        c.setType(body.type);
        c.setCreatedAt(LocalDateTime.now());

        return categories.save(c);
    }

    public List<Category> list() {
        return categories.findAll();
    }
}
