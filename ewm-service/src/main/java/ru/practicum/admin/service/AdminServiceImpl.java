package ru.practicum.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.state.State;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryService.deleteCategory(catId);
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }

    @Override
    public List<EventFullDto> findAllEvents(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return eventService.findAllEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        return compilationService.addCompilation(compilationDto);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationRequest) {
        return compilationService.updateCompilation(compId, compilationRequest);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        return userService.addUser(newUserRequest);
    }

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        return userService.getAll(ids, from, size);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUserById(userId);
    }

}
