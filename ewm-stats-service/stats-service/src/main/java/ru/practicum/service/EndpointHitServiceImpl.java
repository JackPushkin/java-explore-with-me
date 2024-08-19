package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.GetStatsException;
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
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new GetStatsException("start should be before end");
        }
        if (unique) {
            return uris == null
                    ? hitRepository.getAllServicesStatsWithUniqueIp(start, end)
                    : hitRepository.getStatsWithUniqueIp(start, end, uris);
        } else {
            return uris == null
                    ? hitRepository.getAllServicesStats(start, end)
                    : hitRepository.getStats(start, end, uris);
        }
    }
}
