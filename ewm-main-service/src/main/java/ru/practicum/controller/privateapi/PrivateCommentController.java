package ru.practicum.controller.privateapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.model.mapper.CommentMapper;
import ru.practicum.service.interfaces.CommentService;
import ru.practicum.service.interfaces.EventService;
import ru.practicum.service.interfaces.UserService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{userId}")
public class PrivateCommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final EventService eventService;
    private final CommentMapper mapper;

    @PostMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Integer eventId,
            @RequestBody @Valid NewCommentDto commentDto
    ) {
        return mapper.toCommentDto(commentService
                .createComment(userId, eventId, mapper.toCommentFromNewDto(commentDto)));
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Long commentId,
            @RequestBody @Valid UpdateCommentDto commentDto
    ) {
        return mapper.toCommentDto(
                commentService.updateComment(userId, commentId, mapper.toCommentFromUpdateDto(commentDto)));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Long commentId
    ) {
        commentService.deleteComment(false, userId, commentId);
    }
}
