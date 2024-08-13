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
            @RequestParam(defaultValue = "") Set<@Positive Integer> ids,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get users. Query parameters: ids={}, from={}, size={}", ids, from, size);
        return mapper.toUserDtoList(userService.getUsers(ids, from, size));
    }

    @PostMapping
    @Validated(ValidationMarker.OnCreate.class)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create user {}", userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toUserDto(userService.createUser(mapper.toUser(userDto))));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable @Positive Integer userId) {
        log.info("Remove user with id={}", userId);
        userService.removeUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
