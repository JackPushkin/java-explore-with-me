package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.mapper.UserMapper;
import ru.practicum.service.interfaces.UserService;
import ru.practicum.validation.ValidationMarker;

import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(defaultValue = "") Set<@Positive Integer> ids,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get users. Query parameters: ids={}, from={}, size={}", ids, from, size);
        return mapper.toUserDtoList(userService.getUsers(ids, from, size));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Validated(ValidationMarker.OnCreate.class)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create user {}", userDto);
        return mapper.toUserDto(userService.createUser(mapper.toUser(userDto)));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable @Positive Integer userId) {
        log.info("Remove user with id={}", userId);
        userService.removeUser(userId);
    }
}
