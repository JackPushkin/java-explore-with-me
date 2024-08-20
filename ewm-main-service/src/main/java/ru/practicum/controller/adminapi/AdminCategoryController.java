package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Create category: {}", categoryDto);
        return mapper.toCategoryDto(categoryService.createCategory(mapper.toCategory(categoryDto)));

    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable @Positive Integer catId) {
        log.info("Remove category id={}", catId);
        categoryService.removeCategory(catId);
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
