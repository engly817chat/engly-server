package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;
import com.engly.engly_server.repo.CategoriesRepo;
import com.engly.engly_server.service.CategoriesService;
import com.engly.engly_server.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoriesService {
    private final CategoriesRepo categoriesRepo;

    @Override
    public CategoriesDto addCategory(CategoryRequestDto categoryRequestDto) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepo.save(Categories.builder()
                        .description(categoryRequestDto.description())
                        .name(categoryRequestDto.name())
                        .build())
        );
    }

    @Override
    public CategoriesDto updateCategory(String id, CategoryRequestDto categoryRequestDto) {
        return categoriesRepo.findById(id)
                .map(category -> {
                    if (categoryRequestDto.name() != null) category.setName(categoryRequestDto.name());
                    if (categoryRequestDto.description() != null) category.setDescription(categoryRequestDto.description());

                    return CategoryMapper.INSTANCE.toCategoriesDto(categoriesRepo.save(category));
                })
                .orElseThrow(() -> new NotFoundException("Category not found while updating"));
    }

    @Override
    public Page<CategoriesDto> getAllCategories(Pageable pageable) {
        return categoriesRepo.findAll(pageable)
                .map(CategoryMapper.INSTANCE::toCategoriesDto);
    }

    @Override
    public CategoriesDto getCategoryById(String categoryId) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepo.findById(categoryId).orElseThrow(()
                        -> new NotFoundException("Category not found by id: " + categoryId))
        );
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoriesRepo.delete(categoriesRepo.findById(categoryId).orElseThrow(()
                -> new NotFoundException("Category not found while deleting")));
    }

    @Override
    public CategoriesDto findByName(CategoryType name) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepo.findByName(name).orElseThrow(()
                        -> new NotFoundException("Category not found while finding by category type name"))
        );
    }
}
