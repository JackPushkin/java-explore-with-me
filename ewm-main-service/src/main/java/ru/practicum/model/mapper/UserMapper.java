package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto user);

    User toUser(UserShortDto user);

    UserDto toUserDto(User userDto);

    UserShortDto toUserShortDto(User user);

    List<User> toUserListFromUserDto(List<UserDto> userDtoList);

    List<User> toUserListFromShortUserDto(List<UserShortDto> userShortDtoList);

    List<UserDto> toUserDtoList(List<User> users);

    List<UserShortDto> toUserShortDtoList(List<User> users);
}
