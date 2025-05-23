package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;

import java.util.List;

public interface CategoriesService {
    CategoriesDto addCategory(CategoryRequestDto categoryRequestDto);

    CategoriesDto updateCategory(String id, CategoryRequestDto categoryRequestDto);

    List<CategoriesDto> getAllCategories();

    CategoriesDto getCategoryById(String categoryId);

    void deleteCategory(String categoryId);

    CategoriesDto findByName(CategoryType name);
}
