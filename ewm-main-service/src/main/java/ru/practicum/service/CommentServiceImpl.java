package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.CreateCommentException;
import ru.practicum.exception.NotConsistentDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.interfaces.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Comment createComment(Integer userId, Integer eventId, Comment comment) {
        User user = checkUserExist(userId);
        Event event = checkEventExist(eventId);
        comment.setCreator(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Integer userId, Long commentId, Comment comment) {
        checkConsistency(comment.getId(), commentId);
        Comment updatedComment = commentRepository.findByIdAndCreatorId(commentId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Comment id=%d with initiator id=%d not found",
                        commentId, userId)));
        updatedComment.setCreated(LocalDateTime.now());
        updatedComment.setContent(comment.getContent());
        return commentRepository.save(updatedComment);
    }

    @Override
    public void deleteComment(boolean isAdmin, Integer userId, Long commentId) {
        if (isAdmin) {
            commentRepository.deleteById(commentId);
        } else {
            commentRepository.findByIdAndCreatorId(commentId, userId).orElseThrow(() ->
                    new NotFoundException(String.format("Comment id=%d with initiator id=%d not found", commentId, userId)));
            commentRepository.deleteById(commentId);
        }
    }

    @Override
    public List<Comment> getEventComments(Integer eventId, Integer from, Integer size) {
        return commentRepository.findAllByEventId(eventId,
                PageRequest.of(from / size, size, Sort.Direction.DESC, "created")).getContent();
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Comment id=%d not found", commentId)));
    }

    @Override
    public List<Comment> getUserComments(Integer userId, Set<Integer> eventsIds, Integer from, Integer size) {
        return commentRepository.findAllByCreatorIdAndEventIdIn(userId, eventsIds,
                PageRequest.of(from / size, size, Sort.Direction.DESC, "created")).getContent();
    }

    private User checkUserExist(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User id=%d not found", userId)));
    }

    private Event checkEventExist(Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event id=%d not found", eventId)));
        checkEventPublished(event);
        return event;
    }

    private void checkEventPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new CreateCommentException("You can't create comment for not published event");
        }
    }

    private void checkConsistency(Long idFromDto, Long idFromPath) {
        if (idFromDto != null && !idFromDto.equals(idFromPath)) {
            throw new NotConsistentDataException(
                    String.format("Not consistent data. idFromDto=%d, idFromPath=%d", idFromDto, idFromPath));
        }
    }
}
