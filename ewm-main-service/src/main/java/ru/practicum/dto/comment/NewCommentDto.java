package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentDto {

    @NotBlank
    private String content;
}
