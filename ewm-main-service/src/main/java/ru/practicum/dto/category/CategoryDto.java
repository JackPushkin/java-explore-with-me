package ru.practicum.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    private Integer id;

    @NotBlank
    private String name;
}
