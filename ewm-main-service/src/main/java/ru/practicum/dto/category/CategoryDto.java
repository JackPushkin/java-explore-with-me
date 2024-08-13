package ru.practicum.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    private Integer id;

    @NotBlank
    @Length(max = 50)
    private String name;
}
