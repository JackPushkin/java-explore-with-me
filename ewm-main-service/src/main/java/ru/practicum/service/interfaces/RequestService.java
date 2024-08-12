package ru.practicum.service.interfaces;

import ru.practicum.model.Request;

import java.util.List;

public interface RequestService {

    List<Request> getUserRequests(Integer userId);

    Request createRequest(Integer userId, Integer eventId);

    Request cancelRequest(Integer userId, Integer requestId);
}
