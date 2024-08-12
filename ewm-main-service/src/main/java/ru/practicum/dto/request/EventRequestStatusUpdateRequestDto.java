package ru.practicum.dto.request;

import lombok.Data;
import ru.practicum.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequestDto {

    @NotNull
    private List<Integer> requestIds;

    @NotNull
    private RequestStatus status;
}
