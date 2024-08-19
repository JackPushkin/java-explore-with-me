package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto user);

    UserDto toUserDto(User userDto);

    List<UserDto> toUserDtoList(List<User> users);
}
