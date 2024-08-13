package ru.practicum.dto.compilation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.validation.ValidationMarker;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class UpdateCompilationRequestDto {

    private Set<Integer> events;

    private Boolean pinned;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    @Length(max = 50, groups = { ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class })
    private String title;
}
