package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.mapper.UserMapper;
import ru.practicum.service.interfaces.UserService;
import ru.practicum.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
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
            @RequestParam(value = "ids", defaultValue = "") Set<@Positive Integer> ids,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get users. Query parameters: ids={}, from={}, size={}", ids, from, size);
        return mapper.toUserDtoList(userService.getUsers(ids, from, size));
    }

    @PostMapping
    @Validated(value = ValidationMarker.OnCreate.class)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toUserDto(userService.createUser(mapper.toUser(userDto))));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@Positive @PathVariable("userId") Integer userId) {
        log.info("Remove user with id={}", userId);
        userService.removeUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
