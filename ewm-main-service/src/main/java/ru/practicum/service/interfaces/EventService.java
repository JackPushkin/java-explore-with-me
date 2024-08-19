package ru.practicum.service.interfaces;

import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.dto.event.UpdateEventUserRequestDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<Event> getEvents(
            String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, String sort, Integer from, Integer size, List<EventState> state
    );

    Event getEventById(Integer userId, Integer eventId);

    List<Event> getUserEvents(Integer userId, Integer from, Integer size);

    Event createEvent(Integer userId, NewEventDto eventDto, EventMapper mapper);

    Event updateEventByUser(Integer userId, Integer eventId, UpdateEventUserRequestDto requestDto);

    List<Request> getEventRequests(Integer userId, Integer eventId);

    Map<RequestStatus, List<Request>> updateRequestsStatuses(
            Integer userId, Integer eventId, EventRequestStatusUpdateRequestDto updateRequestDto);

    List<Event> getEventsByAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    Event updateEventByAdmin(Integer eventId, UpdateEventAdminRequestDto adminRequestDto);
}
