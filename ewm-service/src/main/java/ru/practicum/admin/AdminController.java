package ru.practicum.admin;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdminService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.state.State;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return adminService.addCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        adminService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        return adminService.updateCategory(catId, newCategoryDto);
    }

    @GetMapping("/events")
    public List<EventFullDto> getAllEvents(@RequestParam(value = "users", required = false) List<Long> users,
                                           @RequestParam(value = "states", required = false) List<State> states,
                                           @RequestParam(value = "categories", required = false) List<Long> categories,
                                           @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                           @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                           @PositiveOrZero
                                           @RequestParam(value = "from", defaultValue = "0", required = false)
                                           Integer from,
                                           @Positive
                                           @RequestParam(value = "size", defaultValue = "10", required = false)
                                           Integer size) {

        return adminService.findAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        return adminService.updateEvent(eventId, updateEventAdminRequest);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers(@RequestParam(value = "ids") List<Long> ids,
                                     @PositiveOrZero
                                     @RequestParam(value = "from", required = false, defaultValue = "0")
                                     Integer from,
                                     @Positive
                                     @RequestParam(value = "size", required = false, defaultValue = "10")
                                     Integer size) {
        return adminService.getAllUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return adminService.addUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return adminService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        adminService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody UpdateCompilationRequest compilationRequest) {
        return adminService.updateCompilation(compId, compilationRequest);
    }
}
