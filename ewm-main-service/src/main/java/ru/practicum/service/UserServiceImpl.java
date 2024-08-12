package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.interfaces.UserService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(Set<Integer> ids, Integer from, Integer size) {
        return ids.isEmpty()
                ? userRepository.findAll()
                : userRepository.findAllByIdIn(ids, PageRequest.of(from / size, size));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d not found", userId)));
        userRepository.deleteById(userId);
    }
}
