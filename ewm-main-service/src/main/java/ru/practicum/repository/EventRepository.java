package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Pair;
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

    @Query("select e from Event as e " +
            "left join fetch Request as r on e.id = r.event.id and r.status = :status " +
            "left join fetch e.initiator as u " +
            "left join fetch e.category as c " +
            "where (:categories is null or e.category.id in (:categories)) " +
            "and (lower(e.annotation) like concat('%',:text,'%') or lower(e.description) like concat('%',:text,'%') ) " +
            "and e.eventDate between :start and :end " +
            "and (:paid is null or e.paid in (:paid)) " +
            "and e.state in (:state) " +
            "group by e.id, u.id, c.id " +
            "having e.participantLimit > count(r.id)")
    List<Event> getAvailableEvents(
            Collection<Integer> categories, String text, LocalDateTime start, LocalDateTime end,
            Boolean paid, Collection<EventState> state, RequestStatus status, Pageable pageable);

    @Query("select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.category " +
            "where (:categories is null or e.category.id in (:categories)) " +
            "and (lower(e.annotation) like concat('%',:text,'%') or lower(e.description) like concat('%',:text,'%') ) " +
            "and e.eventDate between :start and :end " +
            "and (:paid is null or e.paid in (:paid)) " +
            "and e.state in (:state)")
    List<Event> getAllEvents(
            Collection<Integer> categories, String text, LocalDateTime start, LocalDateTime end,
            Boolean paid, Collection<EventState> state, Pageable pageable);

    @Query("select e " +
            "from Event as e " +
            "join fetch e.initiator " +
            "join fetch e.location " +
            "join fetch e.category " +
            "where e.id = :eventId " +
            "and e.state in (:state)")
    Optional<Event> getEventByIdAndState(Integer eventId, Collection<EventState> state);

    @Query("select e from Event as e " +
            "left join fetch e.initiator " +
            "left join fetch e.location " +
            "left join fetch e.category " +
            "where e.id = :eventId " +
            "and e.initiator.id = :initiatorId")
    Optional<Event> getEventByIdAndInitiatorId(Integer eventId, Integer initiatorId);

    @Query("select e from Event as e " +
            "left join fetch e.category " +
            "where e.initiator.id = :initiatorId")
    List<Event> findAllByInitiatorId(Integer initiatorId, Pageable pageable);

    @Query("select new ru.practicum.model.Event(e.id, count(r.id)) " +
            "from Event as e " +
            "left join Request as r on r.event.id = e.id " +
            "and r.status = :status " +
            "group by e.id")
    List<RequestIdCount> getRequestIdCountList(RequestStatus status);

    @Query("select new ru.practicum.model.Event(e.id, count(r.id)) " +
            "from Event as e " +
            "left join Request as r on r.event.id = e.id " +
            "and r.status = :status " +
            "and e.id in (:ids) " +
            "group by e.id")
    List<RequestIdCount> getRequestIdCountListByEventIdIn(RequestStatus status, Collection<Integer> ids);

    @Query("select e from Event as e " +
            "left join fetch e.initiator as i " +
            "left join fetch e.category as c " +
            "left join fetch e.location as l " +
            "where i.id in (:users) " +
            "and (:states is null or e.state in (:states)) " +
            "and (:categories is null or e.category.id in (:categories)) " +
            "and e.eventDate between :start and :end")
    List<Event> findEventsByAdmin(
            Collection<Integer> users, Collection<EventState> states, Collection<Integer> categories,
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select new org.springframework.data.util.Pair(e.id, count(c.id)) " +
            "from Event as e " +
            "left join Comment as c on e.id = c.event.id " +
            "where e.id in (:ids) " +
            "group by e.id")
    List<Pair<Integer, Long>> getCommentsCount(Collection<Integer> ids);

    interface RequestIdCount {

        Integer getId();

        Long getConfirmedRequests();
    }
}
