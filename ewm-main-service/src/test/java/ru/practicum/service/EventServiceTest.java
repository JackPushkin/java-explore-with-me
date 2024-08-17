package ru.practicum.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.mapper.EventMapper;
import ru.practicum.service.interfaces.EventService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceTest {

    private final EventService eventService;
    private final EventMapper mapper;
    private final EntityManager em;

    private LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 12, 0);

    @Test
    public void createEventTest() {
        // Create event
        User user = createUser(1);
        Category category = createCategory(1);
        LocationDto location = createLocationDto(10, 10);
        NewEventDto eventDto = createEventDto("Event", 1, true, 10, category.getId(), location);
        // Save event to DB
        Event event = eventService.createEvent(user.getId(), eventDto, mapper);
        // Get event from DB
        Event eventFromDb = getEvent(event.getId());
        // Check result
        assertThat(eventFromDb.getId(), equalTo(event.getId()));
        assertThat(eventFromDb.getTitle(), equalTo(eventDto.getTitle()));
        assertThat(eventFromDb.getAnnotation(), equalTo(eventDto.getAnnotation()));
        assertThat(eventFromDb.getDescription(), equalTo(eventDto.getDescription()));
        assertThat(eventFromDb.getCreatedOn(), equalTo(event.getCreatedOn()));
        assertThat(eventFromDb.getCategory(), equalTo(category));
        assertThat(eventFromDb.getLocation().getLat(), closeTo(location.getLat(), 0.001));
        assertThat(eventFromDb.getLocation().getLon(), closeTo(location.getLon(), 0.001));
        assertThat(eventFromDb.getInitiator(), equalTo(user));
        assertThat(eventFromDb.getParticipantLimit(), equalTo(eventDto.getParticipantLimit()));
    }

    @Test
    public void getEventByIdTest() {
        // Create event
        User user = createUser(1);
        Event event = createEvent(1, true, 10, true, EventState.PENDING, user);
        // Get event by id
        Event eventFromDb = eventService.getEventById(user.getId(), event.getId());
        // Check result
        assertThat(eventFromDb, equalTo(event));
    }

    private NewEventDto createEventDto(String title, int i, boolean paid, int limit, int catId, LocationDto loc) {
        NewEventDto eventDto = new NewEventDto();
        eventDto.setId(null);
        eventDto.setTitle(title);
        eventDto.setAnnotation("This is event annotation " + i);
        eventDto.setDescription("This is event description" + i);
        eventDto.setEventDate(dateTime.plusYears(i));
        eventDto.setPaid(paid);
        eventDto.setRequestModeration(paid);
        eventDto.setParticipantLimit(limit);
        eventDto.setCategory(catId);
        eventDto.setLocation(loc);
        return eventDto;
    }

    private Category createCategory(int i) {
        Category category = new Category();
        category.setName("category" + i);
        em.persist(category);
        return category;
    }

    private LocationDto createLocationDto(double lat, double lon) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(lat);
        locationDto.setLon(lon);
        return locationDto;
    }

    private Location createLocation(double i) {
        Location location = new Location(null, i, i);
        em.persist(location);
        return location;
    }

    private User createUser(int i) {
        User user = new User();
        user.setName("User" + i);
        user.setEmail("user@email.com" + i);
        em.persist(user);
        return user;
    }

    private Event getEvent(int eventId) {
        return em.createQuery("select e from Event as e where e.id = :eventId", Event.class)
                .setParameter("eventId", eventId).getSingleResult();
    }

    private Event createEvent(int i, boolean paid, int limit, boolean moder, EventState state, User user) {
        Category category = createCategory(i);
        Location location = createLocation(i);
        Event event = Event.builder()
                .id(null).title("Event").annotation("This is event annotation " + i)
                .description("This is event description" + i).category(category).confirmedRequests((long) i)
                .createdOn(dateTime).eventDate(dateTime.plusWeeks(i)).initiator(user).location(location)
                .paid(paid).participantLimit(limit).requestModeration(moder).state(state)
                .build();
        em.persist(event);
        return event;
    }
}
