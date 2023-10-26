package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return new Category(
                newCategoryDto.getName()
        );
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        List<CategoryDto> res = new ArrayList<>();
        for (Category category : categories) {
            res.add(toCategoryDto(category));
        }
        return res;
    }
}
