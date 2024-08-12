package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.mapper.RequestMapper;
import ru.practicum.service.interfaces.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;
    private final RequestMapper mapper;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequest(@Positive @PathVariable("userId") Integer userId) {
        log.info("Get user id={} requests", userId);
        return mapper.toRequestDtoList(requestService.getUserRequests(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @Positive @PathVariable("userId") Integer userId,
            @Positive @RequestParam("eventId") Integer eventId
    ) {
        log.info("Create request by user id={} for event id={}", userId, eventId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toRequestDto(requestService.createRequest(userId, eventId)));
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable("userId") @Positive Integer userId,
            @PathVariable("requestId") @Positive Integer requestId
    ) {
        log.info("Cancel request id={} by user id={}", requestId, userId);
        return mapper.toRequestDto(requestService.cancelRequest(userId, requestId));
    }
}
