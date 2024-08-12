package ru.practicum.dto.request;

import lombok.Data;
import ru.practicum.model.RequestStatus;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {

    private Integer id;

    private Integer event;

    private LocalDateTime created;

    private Integer requester;

    private RequestStatus status;
}
