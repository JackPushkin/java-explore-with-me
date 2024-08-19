package ru.practicum.service.interfaces;

import ru.practicum.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentService {

    Comment createComment(Integer userId, Integer eventId, Comment comment);

    Comment updateComment(Integer userId, Long commentId, Comment comment);

    void deleteComment(boolean isAdmin, Integer userId, Long commentId);

    List<Comment> getEventComments(Integer eventId, Integer from, Integer size);

    Comment getCommentById(Long commentId);

    List<Comment> getUserComments(Integer userId, Set<Integer> eventsIds, Integer from, Integer size);
}
