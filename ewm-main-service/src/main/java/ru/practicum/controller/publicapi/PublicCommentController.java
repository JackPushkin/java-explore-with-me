package ru.practicum.controller.publicapi;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.mapper.CommentMapper;
import ru.practicum.service.interfaces.CommentService;
import ru.practicum.service.interfaces.EventService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {

    private final CommentService commentService;
    private final EventService eventService;
    private final CommentMapper mapper;

    @GetMapping("/{eventId}")
    public List<CommentDto> getEventComments(
            @PathVariable @Positive Integer eventId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return mapper.toCommentDtoList(commentService.getEventComments(eventId, from, size));
    }
}
