package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequesterId(Integer userId);

    List<Request> findAllByEventId(Integer eventId);

    List<Request> findAllByIdIn(Collection<Integer> ids);

    @Query("SELECT COUNT(r.id) " +
           "FROM Request AS r " +
           "WHERE r.event.id = :eventId AND r.status = :state")
    Long getCountOfConfirmedRequests(@Param("eventId") Integer eventId, @Param("state") RequestStatus state);

    @Query("SELECT r " +
           "FROM Request AS r " +
           "JOIN FETCH User AS u ON r.requester.id = u.id AND r.id = :requestId")
    Optional<Request> findRequestById(@Param("requestId") Integer requestId);
}
