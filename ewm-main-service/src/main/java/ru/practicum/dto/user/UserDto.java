package ru.practicum.dto.user;

import lombok.Data;
import ru.practicum.validation.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    @NotNull(groups = ValidationMarker.OnUpdate.class)
    private Integer id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
