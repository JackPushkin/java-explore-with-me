package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.dto.event.UpdateEventUserRequestDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateEventException;
import ru.practicum.exception.UpdateRequestException;
import ru.practicum.model.AdminStateAction;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;
import ru.practicum.model.UserStateAction;
import ru.practicum.model.mapper.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.interfaces.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(
            String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, String sort, Integer from, Integer size, List<EventState> state
    ) {
        text = text == null ? "" : text.toLowerCase();
        if (rangeStart == null || rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.of(3000, 12, 1, 12, 0);
        }
        sort = sort.equals("VIEWS") ? "views" : sort.equals("EVENT_DATE") ? "eventDate" : "id";

        List<Event> events = onlyAvailable
                ? eventRepository.getAvailableEvents(categories, text, rangeStart, rangeEnd, paid, state,
                RequestStatus.CONFIRMED, PageRequest.of(from / size, size, Sort.Direction.DESC, sort))
                : eventRepository.getAllEvents(categories, text, rangeStart, rangeEnd, paid, state,
                PageRequest.of(from / size, size, Sort.Direction.DESC, sort));

        Map<Integer, Long> confirmedRequestsMap =
                getConfirmedRequestsMap(eventRepository.getRequestIdCountList(RequestStatus.CONFIRMED));
        events.forEach(el -> el.setConfirmedRequests(confirmedRequestsMap.get(el.getId())));
        return events;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(Integer userId, Integer eventId) {
        Event event = userId != null
                ? eventRepository.getEventByIdAndInitiatorId(eventId, userId).orElseThrow(
                        () -> new NotFoundException(String.format("Event id=%d with user id=%d not found", eventId, userId)))
                : eventRepository.getEventByIdAndState(eventId, List.of(EventState.PUBLISHED)).orElseThrow(
                        () -> new NotFoundException(String.format("Event with id=%d not found", eventId)));
        event.setConfirmedRequests(requestRepository.getCountOfConfirmedRequests(eventId, RequestStatus.CONFIRMED));
        event.setViews(event.getViews() + 1);
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getUserEvents(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User id=%d not found", userId)));
        List<Event> events = eventRepository
                .findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        Map<Integer, Long> confirmedRequestsMap =
                getConfirmedRequestsMap(eventRepository.getRequestIdCountList(RequestStatus.CONFIRMED));
        events.forEach(el -> el.setConfirmedRequests(confirmedRequestsMap.get(el.getId())));
        return events;
    }

    @Override
    public Event createEvent(Integer userId, NewEventDto eventDto, EventMapper mapper) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Category id=%d not found", eventDto.getCategory())));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException(String.format("Category id=%d not found", eventDto.getCategory())));
        Location location = locationRepository
                .findByLatAndLon(eventDto.getLocation().getLat(), eventDto.getLocation().getLon()).orElse(null);
        Event event = mapper.toEventFromNewDto(eventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location == null
                ? Location.builder().lat(eventDto.getLocation().getLat()).lon(eventDto.getLocation().getLon()).build()
                : location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setParticipantLimit(eventDto.getParticipantLimit() == null ? 0 : eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.getRequestModeration() == null || eventDto.getRequestModeration());
        event.setPaid(eventDto.getPaid() != null && eventDto.getPaid());
        event.setConfirmedRequests(0L);
        event.setViews(0);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEventByUser(Integer userId, Integer eventId, UpdateEventUserRequestDto userRequestDto) {
        Event event;
        event = eventRepository.getEventByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new
                NotFoundException(String.format("Event id=%d with initiator id=%d not found", eventId, userId)));
        checkEventState(event.getState(), eventId);
        event.setState(userRequestDto.getStateAction() == UserStateAction.CANCEL_REVIEW
                ? EventState.CANCELED : EventState.PENDING);
        event.setConfirmedRequests(requestRepository.getCountOfConfirmedRequests(eventId, RequestStatus.CONFIRMED));
        if (userRequestDto.getStateAction() != UserStateAction.CANCEL_REVIEW) {
            buildUpdatedEvent(event, userRequestDto, false);
        }
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getEventRequests(Integer userId, Integer eventId) {
        eventRepository.getEventByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Event id=%d with initiator id=%d not found", eventId, userId)));
        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    public Map<RequestStatus, List<Request>> updateRequestsStatuses(
            Integer userId, Integer eventId, EventRequestStatusUpdateRequestDto updateRequestDto
    ) {
        Event event = eventRepository.getEventByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Event id=%d with initiator id=%d not found", eventId, userId)));
        List<Request> requests = requestRepository.findAllByIdIn(updateRequestDto.getRequestIds());

        if (requests.stream().anyMatch(request -> request.getStatus() != RequestStatus.PENDING)) {
            throw new UpdateRequestException("Status can only be changed for requests with status=PENDING");
        }
        Long countOfConfirmedRequests = requestRepository.getCountOfConfirmedRequests(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() <= countOfConfirmedRequests) {
            throw new UpdateRequestException("The participant limit has been reached");
        }
        if (updateRequestDto.getStatus() == RequestStatus.REJECTED) {
            requests.forEach(el -> el.setStatus(RequestStatus.REJECTED));
        } else {
            for (Request request : requests) {
                if (event.getParticipantLimit() > countOfConfirmedRequests) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    countOfConfirmedRequests++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                }
            }
        }
        requestRepository.saveAll(requests);
        return requests.stream().collect(Collectors.groupingBy(Request::getStatus, Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByAdmin(
            List<Integer> users, List<EventState> states, List<Integer> categories, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Integer from, Integer size
    ) {
        users = users == null || users.isEmpty()
                ? userRepository.findAllId().stream().map(UserRepository.IdsOnly::getId).collect(Collectors.toList())
                : users;
        if (rangeStart == null || rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.of(3000, 12, 1, 12, 0);
        }
        List<Event> events = eventRepository.findEventsByAdmin(
                users, states, categories, rangeStart, rangeEnd, PageRequest.of(from / size, size));
        Map<Integer, Long> confirmedRequestsMap =
                getConfirmedRequestsMap(eventRepository.getRequestIdCountList(RequestStatus.CONFIRMED));
        events.forEach(el -> el.setConfirmedRequests(confirmedRequestsMap.get(el.getId())));
        return events;
    }

    @Override
    public Event updateEventByAdmin(Integer eventId, UpdateEventAdminRequestDto adminRequestDto) {
        Event event = eventRepository.getEventByIdAndState(
                eventId, List.of(EventState.CANCELED, EventState.PENDING, EventState.PUBLISHED)).orElseThrow(() ->
                new NotFoundException(String.format("Event id=%d not found", eventId)));
        AdminStateAction stateAction = adminRequestDto.getStateAction();
        EventState state = event.getState();

        if (stateAction == AdminStateAction.PUBLISH_EVENT) {
            if (state != EventState.PENDING) {
                throw new UpdateEventException("Update event error. You can publish only event with status=PENDING");
            }
            if (LocalDateTime.now().plusHours(1L).isAfter(event.getEventDate())) {
                throw new UpdateEventException(
                        "Update event error. Event date must be no earlier than one hour from the current time");
            }
        }
        if (stateAction == AdminStateAction.REJECT_EVENT && state == EventState.PUBLISHED) {
            throw new UpdateEventException("Update event error. You can't reject event with state=PUBLISHED");
        }
        buildUpdatedEvent(event, adminRequestDto, true);
        event.setState(stateAction == AdminStateAction.PUBLISH_EVENT ? EventState.PUBLISHED : EventState.CANCELED);
        return eventRepository.save(event);
    }

    private Map<Integer, Long> getConfirmedRequestsMap(List<EventRepository.RequestIdCount> requestIdCountList) {
        return requestIdCountList.stream()
                .collect(Collectors.toMap(EventRepository.RequestIdCount::getId,
                        EventRepository.RequestIdCount::getConfirmedRequests, Long::sum));
    }

    private void checkEventState(EventState eventState, Integer eventId) {
        if (eventState == EventState.PUBLISHED) {
            throw new UpdateEventException(
                    String.format("Update event(id=%d) error. You can't update events with state=PUBLISHED", eventId));
        }
    }

    private void checkEventDate(LocalDateTime eventDate, Integer eventId) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusDays(2))) {
            throw new UpdateEventException(String.format(
                    "Update event(id=%d) error. The event date must be no earlier than two days from the current time",
                    eventId));
        }
    }

    private void buildUpdatedEvent(Event event, NewEventDto updateDto, boolean isAdmin) {
        final String annotation = updateDto.getAnnotation();
        final Integer categoryId = updateDto.getCategory();
        final String description = updateDto.getDescription();
        final LocalDateTime eventDate = updateDto.getEventDate();
        final LocationDto locationDto = updateDto.getLocation();
        final Boolean isPaid = updateDto.getPaid();
        final Integer participantLimit = updateDto.getParticipantLimit();
        final Boolean requestModeration = updateDto.getRequestModeration();
        final String title = updateDto.getTitle();

        if (categoryId != null) {
            event.setCategory(categoryRepository.findById(categoryId).orElseThrow(
                    () -> new NotFoundException(String.format("Category id=%d not found", categoryId))));
        }
        if (eventDate != null) {
            if (!isAdmin) checkEventDate(eventDate, event.getId());
            event.setEventDate(eventDate);
        }
        if (locationDto != null) {
            double lat = locationDto.getLat();
            double lon = locationDto.getLon();
            Location location = locationRepository.findByLatAndLon(lat, lon).orElse(null);
            event.setLocation(location != null ? location : Location.builder().lat(lat).lon(lon).build());
        }
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (isPaid != null) event.setPaid(isPaid);
        if (participantLimit != null)
            event.setParticipantLimit(participantLimit);
        if (requestModeration != null)
            event.setRequestModeration(requestModeration);
        if (title != null) event.setTitle(title);
    }
}
