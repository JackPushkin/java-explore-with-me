package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.EventState;
import ru.practicum.model.mapper.EventMapper;
import ru.practicum.service.interfaces.EventService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final EventMapper mapper;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<@Positive Integer> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "true") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get events list. Parameters: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                "onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        return mapper.toEventShortDtoList(eventService.getEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                List.of(EventState.PUBLISHED)));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable("eventId") @Positive Integer eventId) {
        log.info("Get event id={}", eventId);
        return mapper.toEventFullDto(eventService.getEventById(null, eventId));
    }
}
