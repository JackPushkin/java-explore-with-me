package ru.practicum.controller.privateapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
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
    public ResponseEntity<CommentDto> createComment(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Integer eventId,
            @RequestBody @Valid NewCommentDto commentDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toCommentDto(
                        commentService.createComment(userId, eventId, mapper.toCommentFromNewDto(commentDto))));
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Long commentId,
            @RequestBody @Valid NewCommentDto commentDto
    ) {
        return mapper.toCommentDto(
                commentService.updateComment(userId, commentId, mapper.toCommentFromNewDto(commentDto)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable @Positive Integer userId,
            @PathVariable @Positive Long commentId
    ) {
        commentService.deleteComment(false, userId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
