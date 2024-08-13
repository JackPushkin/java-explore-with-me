package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.validation.ValidationMarker;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
public class NewEventDto {

    private Integer id;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String annotation;

    @Positive
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Integer category;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String description;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private LocalDateTime eventDate;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private LocationDto location;

    private Boolean paid;

    @Min(0)
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String title;
}
