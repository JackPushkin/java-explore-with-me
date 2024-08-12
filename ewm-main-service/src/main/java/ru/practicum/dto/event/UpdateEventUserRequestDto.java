package ru.practicum.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.model.UserStateAction;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventUserRequestDto extends NewEventDto {

    @NotNull
    private UserStateAction stateAction;
}
