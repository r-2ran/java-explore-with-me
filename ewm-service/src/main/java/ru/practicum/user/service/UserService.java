package ru.practicum.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import java.util.List;

@Service
public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}
