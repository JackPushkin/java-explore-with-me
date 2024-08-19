package ru.practicum.service.interfaces;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Integer catId);

    Category createCategory(Category category);

    void removeCategory(Integer catId);

    Category updateCategory(Integer catId, CategoryDto categoryDto);
}
