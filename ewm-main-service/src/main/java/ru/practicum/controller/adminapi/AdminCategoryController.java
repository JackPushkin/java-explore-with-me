package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.mapper.CategoryMapper;
import ru.practicum.service.interfaces.CategoryService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Create category: {}", categoryDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toCategoryDto(categoryService.createCategory(mapper.toCategory(categoryDto))));

    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> removeCategory(@PathVariable @Positive Integer catId) {
        log.info("Remove category id={}", catId);
        categoryService.removeCategory(catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(
            @PathVariable @Positive Integer catId,
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        log.info("Update category: {}", categoryDto);
        return mapper.toCategoryDto(categoryService.updateCategory(catId, categoryDto));
    }
}
