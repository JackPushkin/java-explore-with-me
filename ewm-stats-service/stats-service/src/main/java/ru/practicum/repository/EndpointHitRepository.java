package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    // Получить статистику для всех uri без учёта уникальных ip (uri == null, unique == false)

    @Query("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.id)) " +
           "FROM EndpointHit as eh " +
           "WHERE (eh.timestamp BETWEEN :startDateTime AND :endDateTime) " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT(eh.id) DESC")
    List<ViewStats> getAllServicesStats(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    // Получить статистику для всех uri с учётом уникальных ip (uri == null, unique == true)

    @Query("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT (eh.ip))) " +
           "FROM EndpointHit as eh " +
           "WHERE (eh.timestamp BETWEEN :startDateTime AND :endDateTime) " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT(DISTINCT (eh.ip)) DESC")
    List<ViewStats> getAllServicesStatsWithUniqueIp(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Получить статистику для заданных uri без учёта уникальных ip (uri != null, unique == false)

    @Query("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.id)) " +
           "FROM EndpointHit as eh " +
           "WHERE (eh.timestamp BETWEEN :startDateTime AND :endDateTime) " +
                "AND (eh.uri IN (:uris)) " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT(eh.id) DESC")
    List<ViewStats> getStats(LocalDateTime startDateTime, LocalDateTime endDateTime, Collection<String> uris);

    // Получить статистику для заданных uri с учётом уникальных ip (uri != null, unique == true)

    @Query("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT (eh.ip))) " +
           "FROM EndpointHit as eh " +
           "WHERE (eh.timestamp BETWEEN :startDateTime AND :endDateTime) " +
                "AND (eh.uri IN (:uris)) " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT(DISTINCT (eh.ip)) DESC")
    List<ViewStats> getStatsWithUniqueIp(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);
}
