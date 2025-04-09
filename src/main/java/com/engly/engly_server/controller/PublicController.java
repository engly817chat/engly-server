package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.service.CategoriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final CategoriesService categoriesService;

    public PublicController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @Operation(
            summary = "Автентифікація користувача",
            description = """
                         Нумерування сторінок починається з 0 
                         page це номер сторінки
                         size це розмір сторінки
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Повернення всіх категорій.")
            }
    )
    @GetMapping("/get-all-categories")
    public ResponseEntity<PagedModel<EntityModel<CategoriesDto>>> getAll(
            Pageable pageable,
            PagedResourcesAssembler<CategoriesDto> assembler) {
        var allCategories = categoriesService.getAllCategories(pageable);
        return ResponseEntity.ok(assembler.toModel(allCategories));
    }
}
