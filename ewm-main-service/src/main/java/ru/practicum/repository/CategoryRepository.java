package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value = "select count(e.id) " +
            "from categories AS c " +
            "join events e on c.id = e.id_category and c.id = :catId", nativeQuery = true)
    int countOfRelatedEvents(Integer catId);

    @Query("select c from Category as c")
    List<IdsOnly> findAllId();

    interface IdsOnly {

        Integer getId();
    }
}
