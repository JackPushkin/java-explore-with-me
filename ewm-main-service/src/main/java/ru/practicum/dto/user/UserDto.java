package ru.practicum.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import ru.practicum.validation.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
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
