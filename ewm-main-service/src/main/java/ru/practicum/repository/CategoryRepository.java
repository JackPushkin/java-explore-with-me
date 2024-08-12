package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value =
            "SELECT COUNT(e.id) " +
            "FROM categories AS c " +
            "JOIN events e on c.id = e.id_category AND c.id = :catId",
            nativeQuery = true)
    int countOfRelatedEvents(@Param("catId") Integer catId);

    @Query("SELECT c FROM Category AS c")
    List<IdsOnly> findAllId();

    interface IdsOnly {

        Integer getId();
    }
}
