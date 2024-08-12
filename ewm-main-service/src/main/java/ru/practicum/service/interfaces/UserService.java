package ru.practicum.service.interfaces;

import ru.practicum.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getUsers(Set<Integer> ids, Integer from, Integer size);

    User createUser(User user);

    void removeUser(Integer userId);
}
