package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.mapper.EndpointHitMapper;
import ru.practicum.model.mapper.ViewStatsMapper;
import ru.practicum.service.EndpointHitService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EndpointHitController {

    private final EndpointHitService endpointHitService;
    private final EndpointHitMapper hitMapper;
    private final ViewStatsMapper viewMapper;

    @PostMapping("/hit")
    public ResponseEntity<String> createHit(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("Recording request data: hitDto={}", hitDto);
        endpointHitService.createHit(hitMapper.toEndpointHit(hitDto));
        return ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        log.info("Get statistics. Parameters: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return viewMapper.toViewStatsDtoList(endpointHitService.getStats(start, end, uris, unique));
    }
}
