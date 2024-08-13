package ru.practicum.dto.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParticipationRequestDto {

    private Integer id;

    private Integer event;

    private LocalDateTime created;

    private Integer requester;

    private RequestStatus status;
}
