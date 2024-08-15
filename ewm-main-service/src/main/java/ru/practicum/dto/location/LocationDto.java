package ru.practicum.dto.location;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class LocationDto {

    @NotNull
    private double lat;

    @NotNull
    private double lon;
}
