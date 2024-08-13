package ru.practicum.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.AdminStateAction;

@Getter
@Setter
public class UpdateEventAdminRequestDto extends NewEventDto {

    private AdminStateAction stateAction;
}
