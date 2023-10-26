package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto categoryDto);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getAll(int from, int size);

    void deleteCategory(Long categoryId);
}
