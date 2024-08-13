package ru.practicum.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.model.UserStateAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventUserRequestDto extends NewEventDto {

    private UserStateAction stateAction;
}
