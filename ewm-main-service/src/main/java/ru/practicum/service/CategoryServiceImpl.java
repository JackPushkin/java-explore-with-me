package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.CategoryIsNotEmptyException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.interfaces.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Integer catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void removeCategory(Integer catId) {
        getCategoryById(catId);
        if (categoryIsNotEmpty(catId)) {
            throw new CategoryIsNotEmptyException(String.format("The category with id=%d is not empty", catId));
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public Category updateCategory(Integer catId, CategoryDto categoryDto) {
        Category category = getCategoryById(catId);
        category.setName(categoryDto.getName() == null ? category.getName() : categoryDto.getName());
        return categoryRepository.save(category);
    }

    private boolean categoryIsNotEmpty(Integer catId) {
        return categoryRepository.countOfRelatedEvents(catId) > 0;
    }
}
