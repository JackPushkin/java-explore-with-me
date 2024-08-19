package ru.practicum.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.Category;
import ru.practicum.service.interfaces.CategoryService;

import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceTest {

    private final CategoryService categoryService;
    private final EntityManager em;

    @Test
    public void createCategoryTest() {
        // Create category
        Category category = createCategory("Category");
        // Save User to DB
        categoryService.createCategory(category);
        // Get User from DB
        List<Category> categories = getCategoriesFromDB();
        // Check results
        assertThat(categories.size(), equalTo(1));
        assertThat(categories.get(0).getId(), notNullValue());
        assertThat(categories.get(0).getName(), equalTo(category.getName()));
    }

    @Test
    public void getAndUpdateCategoryByIdTest() {
        // Create category
        Category category = createCategory("catName");
        // Save category to DB
        Integer catId = insertCategory(category);
        // Get category from DB
        Category categoryFromDb = categoryService.getCategoryById(catId);
        // Check result
        assertThat(categoryFromDb.getId(), notNullValue());
        assertThat(categoryFromDb.getName(), equalTo("catName"));

        // Create CategoryDto
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("updateCatName");
        // UpdateCategory
        categoryService.updateCategory(catId, categoryDto);
        // Get updated category
        Category updatedCategory = categoryService.getCategoryById(catId);
        // Check results
        assertThat(categoryFromDb.getId(), notNullValue());
        assertThat(categoryFromDb.getName(), equalTo("updateCatName"));
    }

    @Test
    public void getAndRemoveCategoriesTest() {
        // Create categories
        Category category1 = createCategory("cat1");
        Category category2 = createCategory("cat2");
        Category category3 = createCategory("cat3");
        // Save categories to DB
        insertCategories(List.of(category1, category2, category3));
        // Get categories
        List<Category> categories = categoryService.getCategories(0, 10);
        // Check results
        List<Category> list = categories.stream().sorted(Comparator.comparingInt(Category::getId)).toList();
        assertThat(list.size(), equalTo(3));
        assertThat(list.get(0).getId(), notNullValue());
        assertThat(list.get(0).getName(), equalTo(category1.getName()));
        assertThat(list.get(1).getId(), notNullValue());
        assertThat(list.get(1).getName(), equalTo(category2.getName()));
        assertThat(list.get(2).getId(), notNullValue());
        assertThat(list.get(2).getName(), equalTo(category3.getName()));

        // Remove category
        categoryService.removeCategory(list.get(0).getId());
        // Get categories from DB
        List<Category> newCategories = categoryService.getCategories(0, 10);
        // Check results
        assertThat(newCategories.size(), equalTo(2));
        assertThat(newCategories, not(contains(hasProperty("id", is(list.get(0).getId())))));
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setId(null);
        category.setName(name);
        return category;
    }

    private List<Category> getCategoriesFromDB() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    private int insertCategory(Category category) {
        em.persist(category);
        return category.getId();
    }

    private void insertCategories(List<Category> list) {
        list.forEach(em::persist);
    }
}
