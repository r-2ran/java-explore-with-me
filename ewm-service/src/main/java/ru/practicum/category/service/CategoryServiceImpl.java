package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

import static ru.practicum.category.mapper.CategoryMapper.*;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        try {
            return toCategoryDto(categoryRepository.save(toCategory(categoryDto)));
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto) {
        Category category = checkCategory(categoryId);
        category.setName(category.getName());
        try {
            return toCategoryDto(categoryRepository.save(toCategory(categoryDto)));
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return toCategoryDto(checkCategory(categoryId));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        return toCategoryDtoList(categoryRepository.findAll(pageable).getContent());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        checkCategory(categoryId);
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new AccessDeniedException("cannot delete category cause have events");
        }
        categoryRepository.deleteById(categoryId);
    }

    private Category checkCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("no category id = %d", id))
        );
    }
}
