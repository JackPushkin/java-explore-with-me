package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndCreatorId(Long commentId, Integer userId);

    Page<Comment> findAllByEventId(Integer eventId, Pageable pageable);

    @Query("select c " +
            "from Comment as c " +
            "where c.creator.id = :userId " +
            "and (:eventsIds is null or c.event.id in (:eventsIds))")
    Page<Comment> findAllByCreatorIdAndEventIdIn(Integer userId, Collection<Integer> eventsIds, Pageable pageable);
}
