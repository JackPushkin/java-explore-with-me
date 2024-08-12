package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.model.EventState;
import ru.practicum.model.mapper.EventMapper;
import ru.practicum.service.interfaces.EventService;
import ru.practicum.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;
    private final EventMapper mapper;

    @GetMapping
    public List<EventFullDto> getEventsList(
            @RequestParam(value = "users", required = false) List<@Positive Integer> users,
            @RequestParam(value = "states", required = false) List<EventState> states,
            @RequestParam(value = "categories", required = false) List<@Positive Integer> categories,
            @RequestParam(value = "rangeStart", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get events list. Params: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return mapper.toEventFullDtoList(eventService.getEventsByAdmin(
                users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    @Validated(ValidationMarker.OnUpdate.class)
    public EventFullDto updateEvent(
            @PathVariable("eventId") @Positive(groups = ValidationMarker.OnUpdate.class) Integer eventId,
            @RequestBody @Valid UpdateEventAdminRequestDto adminRequestDto
    ) {
        return mapper.toEventFullDto(eventService.updateEventByAdmin(eventId, adminRequestDto));
    }
}
