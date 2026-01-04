package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services/repositories, and returns DTOs.

import com.epicode.NearMissHub.payloads.response.CategoryResponse;
import com.epicode.NearMissHub.payloads.request.CreateCategoryRequest;
import com.epicode.NearMissHub.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return service.list().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getType(), c.getCreatedAt()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest body) {
        var c = service.create(body);
        return new ResponseEntity<>(
                new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getType(), c.getCreatedAt()),
                HttpStatus.CREATED
        );
    }


}
