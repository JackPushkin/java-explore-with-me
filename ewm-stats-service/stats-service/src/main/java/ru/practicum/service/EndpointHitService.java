package ru.practicum.service;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {

    void createHit(EndpointHit hit);

    List<ViewStats> getStats(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris, Boolean unique);
}
