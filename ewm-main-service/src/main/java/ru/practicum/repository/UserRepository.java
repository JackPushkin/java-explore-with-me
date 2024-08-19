package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByIdIn(Collection<Integer> ids, Pageable pageable);

    @Query("select u from User as u")
    List<IdsOnly> findAllId();

    interface IdsOnly {

        Integer getId();
    }
}
