package ru.practicum.dto.category;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryDto {

    private Integer id;

    @NotBlank
    @Length(max = 50)
    private String name;
}
