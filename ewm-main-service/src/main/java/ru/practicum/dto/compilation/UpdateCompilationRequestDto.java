package ru.practicum.dto.compilation;

import lombok.Data;
import ru.practicum.validation.ValidationMarker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class UpdateCompilationRequestDto {

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Set<Integer> events;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Boolean pinned;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String title;
}
