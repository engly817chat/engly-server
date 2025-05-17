package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.CategoryRequestDto;
import com.engly.engly_server.repository.CategoriesRepository;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.mapper.CategoryMapper;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Override
    @CacheEvict(value = "categoryRequestDto", allEntries = true)
    @Caching(
            put = {
                    @CachePut(value = "categoryById", key = "#result.id"),
                    @CachePut(value = "categoryByName", key = "#result.name.toString()")
            },
            evict = {
                    @CacheEvict(value = "allCategories", allEntries = true)
            }
    )
    public CategoriesDto addCategory(CategoryRequestDto categoryRequestDto) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepository.save(Categories.builder()
                        .description(categoryRequestDto.description())
                        .name(categoryRequestDto.name())
                        .build())
        );
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "categoryById", key = "#id"),
                    @CachePut(value = "categoryByName", key = "#result.name.toString()")
            },
            evict = {
                    @CacheEvict(value = "allCategories", allEntries = true)
            }
    )
    public CategoriesDto updateCategory(String id, CategoryRequestDto categoryRequestDto) {
        return categoriesRepository.findById(id)
                .map(category -> {
                    if (FieldUtil.isValid(categoryRequestDto.name())) category.setName(categoryRequestDto.name());
                    if (FieldUtil.isValid(categoryRequestDto.description())) category.setDescription(categoryRequestDto.description());

                    return CategoryMapper.INSTANCE.toCategoriesDto(categoriesRepository.save(category));
                })
                .orElseThrow(() -> new NotFoundException("Category not found while updating"));
    }

    @Override
    @Cacheable(value = "allCategories", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<CategoriesDto> getAllCategories() {
        return categoriesRepository.findAll()
                .stream()
                .map(CategoryMapper.INSTANCE::toCategoriesDto)
                .toList();
    }

    @Override
    @Cacheable(value = "categoryById", key = "#categoryId")
    @Transactional(readOnly = true)
    public CategoriesDto getCategoryById(String categoryId) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepository.findById(categoryId).orElseThrow(()
                        -> new NotFoundException("Category not found by id: " + categoryId))
        );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categoryById", key = "#categoryId"),
            @CacheEvict(value = "allCategories", allEntries = true)
    })
    public void deleteCategory(String categoryId) {
        categoriesRepository.delete(categoriesRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException("Category not found while deleting")));
    }

    @Override
    @RateLimiter(name = "CategoryService")
    @Cacheable(value = "categoryByName", key = "#name.toString()")
    @Transactional(readOnly = true)
    public CategoriesDto findByName(CategoryType name) {
        return CategoryMapper.INSTANCE.toCategoriesDto(
                categoriesRepository.findByName(name).orElseThrow(()
                        -> new NotFoundException("Category not found while finding by category type name"))
        );
    }
}
