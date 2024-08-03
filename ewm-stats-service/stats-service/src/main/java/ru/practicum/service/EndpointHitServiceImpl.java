package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository hitRepository;

    @Override
    @Transactional
    public void createHit(EndpointHit hit) {
        hitRepository.save(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getStats(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            List<String> uris,
            Boolean unique
    ) {
        if (startDateTime.isAfter(endDateTime)) {
            throw new RuntimeException("start should be before end");
        }
        if (unique) {
            return uris == null
                    ? hitRepository.getAllServicesStatsWithUniqueIp(startDateTime, endDateTime)
                    : hitRepository.getStatsWithUniqueIp(startDateTime, endDateTime, uris);
        } else {
            return uris == null
                    ? hitRepository.getAllServicesStats(startDateTime, endDateTime)
                    : hitRepository.getStats(startDateTime, endDateTime, uris);
        }
    }
}
