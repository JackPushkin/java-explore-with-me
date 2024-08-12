package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.mapper.CategoryMapper;
import ru.practicum.service.interfaces.CategoryService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    @GetMapping
    public List<CategoryDto> getCategories(
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get categories. Query parameters: from={}, size={}", from, size);
        return mapper.toCategoryDtoList(categoryService.getCategories(from, size));
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@Positive @PathVariable("catId") Integer catId) {
        log.info("Get category with id={}", catId);
        return mapper.toCategoryDto(categoryService.getCategoryById(catId));
    }
}
