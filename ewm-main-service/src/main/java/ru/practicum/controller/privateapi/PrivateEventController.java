package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequestDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.mapper.EventMapper;
import ru.practicum.model.mapper.RequestMapper;
import ru.practicum.service.interfaces.EventService;
import ru.practicum.validation.ValidationMarker;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @GetMapping
    public List<EventShortDto> getEvents(
            @PathVariable @Positive Integer userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get events by user id={}. Parameters: from={}, size={}", userId, from, size);
        return eventMapper.toEventShortDtoList(eventService.getUserEvents(userId, from, size));
    }

    @PostMapping
    @Validated(ValidationMarker.OnCreate.class)
    public ResponseEntity<EventFullDto> createEvent(
            @PathVariable @Positive(groups = ValidationMarker.OnCreate.class) Integer userId,
            @RequestBody @Valid NewEventDto eventDto
    ) {
        log.info("Create event={} by user id={}", eventDto, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventMapper.toEventFullDto(eventService.createEvent(userId, eventDto, eventMapper)));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(
            @PathVariable("userId") @Positive Integer userId,
            @PathVariable("eventId") @Positive Integer eventId
    ) {
        log.info("Get event id={} by user id={}", eventId, userId);
        return eventMapper.toEventFullDto(eventService.getEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    @Validated(ValidationMarker.OnUpdate.class)
    public EventFullDto updateEvent(
            @PathVariable @Positive(groups = ValidationMarker.OnUpdate.class) Integer userId,
            @PathVariable @Positive(groups = ValidationMarker.OnUpdate.class) Integer eventId,
            @RequestBody @Valid UpdateEventUserRequestDto requestDto
    ) {
        log.info("Update event id={} by user id={}. Update object={}", eventId, userId, requestDto);
        return eventMapper.toEventFullDto(eventService.updateEventByUser(userId, eventId, requestDto));
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Integer eventId
    ) {
        log.info("Get requests for event id={} by user id={}", eventId, userId);
        return requestMapper.toRequestDtoList(eventService.getEventRequests(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto getEventRequests(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Integer eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequestDto updateRequestDto
    ) {
        log.info("Update request statuses for event(id={}) by user(id={}). Update object: {}",
                eventId, userId, updateRequestDto);
        Map<RequestStatus, List<Request>> requestsMap =
                eventService.updateRequestsStatuses(userId, eventId, updateRequestDto);
        return new EventRequestStatusUpdateResultDto(
                requestMapper.toRequestDtoList(requestsMap.getOrDefault(RequestStatus.CONFIRMED, new ArrayList<>())),
                requestMapper.toRequestDtoList(requestsMap.getOrDefault(RequestStatus.REJECTED, new ArrayList<>())));
    }
}
