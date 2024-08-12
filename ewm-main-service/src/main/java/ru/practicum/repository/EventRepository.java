package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findAllByIdIn(Collection<Integer> ids);

    @Query("SELECT e FROM Event AS e " +
            "LEFT JOIN FETCH Request AS r ON e.id = r.event.id and r.status = :requestStatus " +
            "LEFT JOIN FETCH e.initiator AS u " +
            "LEFT JOIN FETCH e.category AS c " +
            "WHERE e.category.id IN (:categories) " +
            "AND (LOWER(e.annotation) LIKE CONCAT('%',:text,'%') OR LOWER(e.description) LIKE CONCAT('%',:text,'%')) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND e.paid IN (:paid) " +
            "AND e.state IN (:eventState) " +
            "GROUP BY e.id, u.id, c.id " +
            "HAVING e.participantLimit > COUNT(r.id)")
    List<Event> getAvailableEvents(
            @Param("categories") Collection<Integer> categories, @Param("text") String text,
            @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Collection<Boolean> paid, @Param("eventState") Collection<EventState> state,
            @Param("requestStatus") RequestStatus status, Pageable pageable
    );

    @Query("SELECT e FROM Event AS e " +
            "LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category " +
            "WHERE e.category.id IN (:categories) " +
                "AND (LOWER(e.annotation) LIKE CONCAT('%',:text,'%') OR LOWER(e.description) LIKE CONCAT('%',:text,'%')) " +
                "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
                "AND e.paid IN (:paid) " +
                "AND e.state IN (:eventState)")
    List<Event> getAllEvents(
            @Param("categories") Collection<Integer> categories, @Param("text") String text,
            @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("paid") Collection<Boolean> paid, @Param("eventState") Collection<EventState> state, Pageable pageable
    );

    @Query("SELECT e " +
            "FROM Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.category " +
            "WHERE e.id = :eventId AND e.state IN (:state)")
    Optional<Event> getEventByIdAndState(@Param("eventId") Integer eventId, @Param("state") Collection<EventState> state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.category " +
            "WHERE e.id = :eventId AND e.initiator.id = :initiatorId")
    Optional<Event> getEventByIdAndInitiatorId(
            @Param("eventId") Integer eventId, @Param("initiatorId") Integer initiatorId);

    @Query("SELECT e FROM Event AS e " +
            "LEFT JOIN FETCH e.category " +
            "WHERE e.initiator.id = :initiatorId")
    List<Event> findAllByInitiatorId(@Param("initiatorId") Integer initiatorId, Pageable pageable);

    @Query("SELECT new ru.practicum.model.Event(e.id, COUNT(r.id)) " +
            "FROM Event AS e LEFT JOIN Request AS r ON r.event.id = e.id and r.status = :status group by e.id")
    List<RequestIdCount> getRequestIdCountList(@Param("status") RequestStatus status);

    @Query("SELECT new ru.practicum.model.Event(e.id, COUNT(r.id)) " +
            "FROM Event AS e LEFT JOIN Request AS r ON r.event.id = e.id " +
                "and r.status = :status " +
                "and e.id in (:eventIds) " +
            "group by e.id")
    List<RequestIdCount> getRequestIdCountListByEventIdIn(
            @Param("status") RequestStatus status, @Param("eventIds") Collection<Integer> ids);

    @Query("select e from Event as e " +
            "join fetch e.initiator as i " +
            "join fetch e.category as c " +
            "join fetch e.location as l " +
            "where i.id in (:users) " +
                "and e.state in (:states) " +
                "and c.id in (:categories) " +
                "and e.eventDate between :start and :end")
    List<Event> findEventsByAdmin(
            @Param("users") Collection<Integer> users, @Param("states") Collection<EventState> states,
            @Param("categories") Collection<Integer> categories, @Param("start") LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd, Pageable pageable);

    interface RequestIdCount {
        Integer getId();

        Long getConfirmedRequests();
    }
}
