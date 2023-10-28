package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getAll(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    CompilationDto getById(@PathVariable Long compId) {
        return compilationService.getById(compId);
    }
}
