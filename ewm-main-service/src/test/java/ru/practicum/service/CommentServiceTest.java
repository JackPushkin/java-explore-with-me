package ru.practicum.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.service.interfaces.CommentService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceTest {

    private final CommentService commentService;
    private final EntityManager em;

    private LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 12, 0);

    @Test
    public void createAndUpdateCommentTest() {
        // Create comment
        User creator = createUser("User", "user@email.com");
        Event event = createEvent(1);
        Comment comment = createComment(1, creator, event);
        // Save comment to DB
        Comment savedComment = commentService.createComment(creator.getId(), event.getId(), comment);
        // Get comment from DB
        Comment result = getComment(savedComment.getId());
        // Check result
        assertThat(result.getId(), equalTo(savedComment.getId()));
        assertThat(result.getContent(), equalTo(comment.getContent()));
        assertThat(result.getCreator().getId(), equalTo(creator.getId()));
        assertThat(result.getEvent().getId(), equalTo(event.getId()));
        assertThat(result.getCreated().truncatedTo(ChronoUnit.SECONDS),
                equalTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        // Update comment
        Comment update = new Comment();
        update.setContent("UpdatedContent");
        commentService.updateComment(creator.getId(), result.getId(), update);
        // Get updated comment from DB
        Comment updatedComment = getComment(result.getId());
        // Check result
        assertThat(updatedComment.getId(), equalTo(result.getId()));
        assertThat(updatedComment.getContent(), equalTo(update.getContent()));
        assertThat(updatedComment.getCreated().truncatedTo(ChronoUnit.SECONDS),
                equalTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    public void getAndDeleteEventComments() {
        // Create event
        User creator = createUser("User", "user@email.com");
        Event event = createEvent(1);
        // Create some comments
        Comment comment1 = createComment(1, creator, event);
        Comment comment2 = createComment(2, creator, event);
        Comment comment3 = createComment(3, creator, event);
        // Save comments to DB
        Comment newComment1 = commentService.createComment(creator.getId(), event.getId(), comment1);
        Comment newComment2 = commentService.createComment(creator.getId(), event.getId(), comment2);
        Comment newComment3 = commentService.createComment(creator.getId(), event.getId(), comment3);
        // Get comments from DB
        List<Comment> comments = commentService.getEventComments(event.getId(), 0, 10);
        // Check result
        comments = comments.stream()
                .sorted((c1, c2) -> String.CASE_INSENSITIVE_ORDER.compare(c1.getContent(), c2.getContent())).toList();
        assertThat(comments.size(), equalTo(3));
        assertThat(comments.get(0).getId(), notNullValue());
        assertThat(comments.get(0).getContent(), equalTo(comment1.getContent()));
        assertThat(comments.get(1).getId(), notNullValue());
        assertThat(comments.get(1).getContent(), equalTo(comment2.getContent()));
        assertThat(comments.get(2).getId(), notNullValue());
        assertThat(comments.get(2).getContent(), equalTo(comment3.getContent()));

        // Delete comment by comment creator
        commentService.deleteComment(false, creator.getId(), newComment1.getId());
        // Get comments from DB
        comments = commentService.getEventComments(event.getId(), 0, 10);
        // Check result
        assertThat(comments.size(), equalTo(2));
        assertThat(comments, not(contains(hasProperty("content", is(comment1.getContent())))));

        // Delete comment by another user
        User anotherUser = createUser("AnotherUser", "anotherUser@email.com");
        assertThrows(NotFoundException.class,
                () -> commentService.deleteComment(false, anotherUser.getId(), newComment2.getId()));

        // Delete comment by admin
        commentService.deleteComment(true, null, newComment3.getId());
        // Get comments from DB
        comments = commentService.getEventComments(event.getId(), 0, 10);
        // Check result
        assertThat(comments.size(), equalTo(1));
        assertThat(comments, not(contains(hasProperty("content", is(comment3.getContent())))));
    }

    @Test
    public void getCommentByIdTest() {
        // Create comment
        User creator = createUser("User", "user@email.com");
        Event event = createEvent(1);
        Comment comment = createComment(1, creator, event);
        // Save comment to DB
        Comment savedComment = commentService.createComment(creator.getId(), event.getId(), comment);
        // Get comment by id
        Comment commentFromDb = commentService.getCommentById(savedComment.getId());
        // Check result
        assertThat(commentFromDb.getId(), equalTo(savedComment.getId()));
        assertThat(commentFromDb.getContent(), equalTo(comment.getContent()));
        assertThat(commentFromDb.getCreator().getId(), equalTo(creator.getId()));
        assertThat(commentFromDb.getEvent().getId(), equalTo(event.getId()));
        assertThat(commentFromDb.getCreated().truncatedTo(ChronoUnit.SECONDS),
                equalTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    public void getUserCommentsTest() {
        // Create some users
        User user1 = createUser("User1", "user1@email.com");
        User user2 = createUser("User2", "user2@email.com");
        // Create some events
        Event event1 = createEvent(1);
        Event event2 = createEvent(2);
        // Create some comments
        Comment comment1 = createComment(1, user1, event1);
        Comment comment2 = createComment(2, user1, event2);
        Comment comment3 = createComment(3, user2, event2);
        // Save comments to DB
        Comment savedComment1 = commentService.createComment(user1.getId(), event1.getId(), comment1);
        Comment savedComment2 = commentService.createComment(user1.getId(), event2.getId(), comment2);
        Comment savedComment3 = commentService.createComment(user2.getId(), event2.getId(), comment3);

        // Get all comments by user1 for event1
        List<Comment> user1event1Comments = commentService.getUserComments(user1.getId(), Set.of(event1.getId()), 0, 10);
        // Check result
        assertThat(user1event1Comments.size(), equalTo(1));
        assertThat(user1event1Comments.get(0), equalTo(savedComment1));

        // Get all comments by user1 for all events
        List<Comment> user1Comments = commentService.getUserComments(user1.getId(), null, 0, 10);
        // Check result
        user1Comments = user1Comments.stream()
                .sorted((c1, c2) -> String.CASE_INSENSITIVE_ORDER.compare(c1.getContent(), c2.getContent())).toList();
        assertThat(user1Comments.size(), equalTo(2));
        assertThat(user1Comments.get(0), equalTo(savedComment1));
        assertThat(user1Comments.get(1), equalTo(savedComment2));

        // Get all comments by user2 for all events
        List<Comment> user2Comments = commentService.getUserComments(user2.getId(), null, 0, 10);
        // Check result
        assertThat(user2Comments.size(), equalTo(1));
        assertThat(user2Comments.get(0), equalTo(savedComment3));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        em.persist(user);
        return user;
    }

    private Comment createComment(int i, User user, Event event) {
        Comment comment = new Comment();
        comment.setContent("Comment" + i);
        comment.setCreated(dateTime);
        comment.setCreator(user);
        comment.setEvent(event);
        return comment;
    }

    private Category createCategory(int i) {
        Category category = new Category();
        category.setName("category" + i);
        em.persist(category);
        return category;
    }

    private Location createLocation(double i) {
        Location location = new Location(null, i, i);
        em.persist(location);
        return location;
    }

    private Event createEvent(int i) {
        User user = createUser("User" + i, "user@email.com" + i);
        Category category = createCategory(i);
        Location location = createLocation(i);
        Event event = Event.builder()
                .id(null).title("Event").annotation("This is event annotation " + i)
                .description("This is event description" + i).category(category).confirmedRequests((long) i)
                .createdOn(dateTime).eventDate(dateTime.plusWeeks(i)).initiator(user).location(location)
                .paid(false).participantLimit(i).requestModeration(true).state(EventState.PUBLISHED)
                .build();
        em.persist(event);
        return event;
    }

    private Comment getComment(long commentId) {
        return em.createQuery("select c from Comment as c where c.id = :commentId", Comment.class)
                .setParameter("commentId", commentId).getSingleResult();
    }
}
