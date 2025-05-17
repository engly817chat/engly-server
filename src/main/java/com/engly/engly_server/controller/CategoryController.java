package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;
import com.engly.engly_server.service.common.CategoriesService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoriesService categoriesService;

    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PreAuthorize("hasAuthority('SCOPE_CREATE_GLOBAL')")
    @PostMapping("/create")
    @RateLimiter(name = "CategoryController")
    public ResponseEntity<CategoriesDto> addCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        return new ResponseEntity<>(categoriesService.addCategory(categoryRequestDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    @PutMapping("/update/{id}")
    @RateLimiter(name = "CategoryController")
    public ResponseEntity<CategoriesDto> updateCategory(@PathVariable String id, @RequestBody CategoryRequestDto categoryRequestDto) {
        return new ResponseEntity<>(categoriesService.updateCategory(id, categoryRequestDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    @DeleteMapping("/{id}")
    @RateLimiter(name = "CategoryController")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoriesService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriesDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoriesService.getCategoryById(id));
    }
}
