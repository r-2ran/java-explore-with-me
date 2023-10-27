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
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                @Positive  @RequestParam(required = false, defaultValue = "10") int size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    CompilationDto getById(@PathVariable Long compId) {
        return compilationService.getById(compId);
    }

}
