package ru.practicum.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.UserStateAction;

@Getter
@Setter
public class UpdateEventUserRequestDto extends NewEventDto {

    private UserStateAction stateAction;
}
