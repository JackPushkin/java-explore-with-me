package ru.practicum.dto.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequestDto {

    @NotNull
    private List<Integer> requestIds;

    @NotNull
    private RequestStatus status;
}
