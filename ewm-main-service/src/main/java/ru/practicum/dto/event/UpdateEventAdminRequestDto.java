package ru.practicum.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.model.AdminStateAction;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventAdminRequestDto extends NewEventDto {

    @NotNull
    private AdminStateAction stateAction;
}
