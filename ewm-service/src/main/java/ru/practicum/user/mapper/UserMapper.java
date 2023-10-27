package ru.practicum.user.mapper;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserDto> toDtoList(List<User> users) {
        List<UserDto> res = new ArrayList<>();
        for (User user : users) {
            res.add(toUserDto(user));
        }
        return res;
    }

    public static UserShortDto toShortUserDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static User toUser(NewUserRequest newUserRequest) {
        return new User(
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }
}
