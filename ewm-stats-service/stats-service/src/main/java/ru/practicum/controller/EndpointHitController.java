package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.mapper.EndpointHitMapper;
import ru.practicum.model.mapper.ViewStatsMapper;
import ru.practicum.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EndpointHitController {

    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    public String createHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("Запись данных о запросе hitDto={}", hitDto);
        endpointHitService.createHit(EndpointHitMapper.toEndpointHit(hitDto));
        return "Информация сохранена";
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
            @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique
    ) {
        log.info("Получение статистики ");
        return ViewStatsMapper.toViewStatsDto(endpointHitService.getStats(startDateTime, endDateTime, uris, unique));
    }
}
