package ru.practicum.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.model.AdminStateAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventAdminRequestDto extends NewEventDto {

    private AdminStateAction stateAction;
}
