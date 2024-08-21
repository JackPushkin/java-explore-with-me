package ru.practicum.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;

    private String content;

    private LocalDateTime created;

    private Integer userId;

    private Integer eventId;
}
