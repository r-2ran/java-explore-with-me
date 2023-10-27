package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDto> getAll(@PositiveOrZero
                                    @RequestParam(value = "from", defaultValue = "0", required = false)
                                    Integer from,
                                    @Positive
                                    @RequestParam(value = "size", defaultValue = "10", required = false)
                                    Integer size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getCategoryById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
