package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.CreateRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UpdateRequestException;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.interfaces.RequestService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<Request> getUserRequests(Integer userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d not found", userId)));
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public Request createRequest(Integer userId, Integer eventId) {
        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%d not found", eventId)));
        checkRequesterOwnEvent(userId, event.getInitiator().getId(), eventId);
        checkUnpublishedEvent(event.getState(), eventId);

        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0) {
            Long countOfConfirmedRequests = requestRepository.getCountOfConfirmedRequests(eventId, RequestStatus.CONFIRMED);
            checkConfirmedRequestsLimit(event.getParticipantLimit(), countOfConfirmedRequests);
        }
        Request request = Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(RequestStatus.PENDING)
                .build();

        if (event.getParticipantLimit().equals(0) || !event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return requestRepository.save(request);
    }

    @Override
    public Request cancelRequest(Integer userId, Integer requestId) {
        Request request = requestRepository.findRequestById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Request with id=%d not found", requestId)));
        User requester = request.getRequester();
        checkUpdateRequestByRequester(requester.getId(), userId);
        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    private void checkRequesterOwnEvent(Integer requesterId, Integer eventOwnerId, Integer eventId) {
        if (requesterId.equals(eventOwnerId)) {
            throw new CreateRequestException(String.format(
                    "Create request error. Requester(id=%d) is event(id=%d) initiator", requesterId, eventId));
        }
    }

    private void checkUnpublishedEvent(EventState state, Integer eventId) {
        if (state != EventState.PUBLISHED) {
            throw new CreateRequestException(String.format(
                    "Create request error. Event(id=%d) is not published", eventId));
        }
    }

    private void checkConfirmedRequestsLimit(Integer limit, Long countOfConfirmedRequests) {
        if (limit <= countOfConfirmedRequests) {
            throw new CreateRequestException("Create request error. The number of allowed requests has been exceeded");
        }
    }

    private void checkUpdateRequestByRequester(Integer requesterId, Integer userId) {
        if (!requesterId.equals(userId)) {
            throw new UpdateRequestException(String.format(
                    "Update request error. User(id=%d) is not a requester(id=%d)", userId, requesterId));
        }
    }
}
