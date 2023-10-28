package ru.practicum.user.admin;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(@RequestParam(value = "ids", required = false) List<Long> ids,
                             @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return userService.getAll(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUser) {
        return userService.addUser(newUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }
}
