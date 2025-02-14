package ru.practicum.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import ru.practicum.validation.ValidationMarker;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Validated(ValidationMarker.OnCreate.class)
public class UserDto {
    private Integer id;

    @NotBlank
    @Length(min = 2, max = 250)
    private String name;

    @Email
    @NotNull
    @Length(min = 6, max = 254)
    private String email;
}
