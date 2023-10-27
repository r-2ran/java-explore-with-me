package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.AlreadyExistException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.user.mapper.UserMapper.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        try {
            return toUserDto(userRepository.save(toUser(newUserRequest)));
        } catch (RuntimeException e) {
            throw new AlreadyExistException(String.format("user with mail %s already exist",
                    newUserRequest.getEmail()));
        }
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        if (ids.isEmpty()) {
            return toDtoList(userRepository.findAll(pageable).getContent());
        }

        return toDtoList(userRepository.findByIdIn(ids, pageable));
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}
