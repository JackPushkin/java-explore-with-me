package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequesterId(Integer userId);

    List<Request> findAllByEventId(Integer eventId);

    List<Request> findAllByIdIn(Collection<Integer> ids);

    @Query("select count(r.id) " +
           "from Request as r " +
           "where r.event.id = :eventId and r.status = :state")
    Long getCountOfConfirmedRequests(Integer eventId, RequestStatus state);

    @Query("select r " +
           "from Request as r " +
           "join fetch User as u on r.requester.id = u.id and r.id = :requestId")
    Optional<Request> findRequestById(Integer requestId);
}
