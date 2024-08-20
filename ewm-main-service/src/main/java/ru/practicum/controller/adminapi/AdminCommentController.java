package ru.practicum.controller.adminapi;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.mapper.CommentMapper;
import ru.practicum.service.interfaces.CommentService;
import ru.practicum.service.interfaces.EventService;
import ru.practicum.service.interfaces.UserService;

import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final EventService eventService;
    private final CommentMapper mapper;

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable @Positive Long commentId) {
        log.info("Get comment id={}", commentId);
        return mapper.toCommentDto(commentService.getCommentById(commentId));
    }

    @GetMapping("/users/{userId}")
    public List<CommentDto> getUserComments(
            @PathVariable @Positive Integer userId,
            @RequestParam(name = "ids", required = false) Set<@Positive Integer> eventsIds,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get user`s comments. Params: userId={}, eventsId={}, from={}, size={}", userId, eventsIds, from, size);
        return mapper.toCommentDtoList(commentService.getUserComments(userId, eventsIds, from, size));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        log.info("Delete comment id={}", commentId);
        commentService.deleteComment(true, null, commentId);
    }
}
