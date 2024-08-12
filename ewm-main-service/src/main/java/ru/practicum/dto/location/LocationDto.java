package ru.practicum.dto.location;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LocationDto {

    @NotNull
    private double lat;

    @NotNull
    private double lon;
}
