package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
public class CompilationDto {

    private Integer id;

    private List<EventShortDto> events;

    private boolean pinned;

    private String title;
}
