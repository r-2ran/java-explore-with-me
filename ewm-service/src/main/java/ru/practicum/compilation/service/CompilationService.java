package ru.practicum.compilation.service;

import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

@Service
public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long compilationId);

    void deleteCompilation(Long compilationId);
}
