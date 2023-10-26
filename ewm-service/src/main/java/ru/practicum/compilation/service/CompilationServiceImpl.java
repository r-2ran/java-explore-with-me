package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;

import static ru.practicum.compilation.CompilationMapper.*;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = to(compilationDto);
        compilation.setEvents(new HashSet<>(eventRepository.findAllById(compilationDto.getEvents())));
        return toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = checkCompilation(compilationId);
        if (!compilation.getEvents().isEmpty()) {
            compilation.setEvents(
                    new HashSet<>(eventRepository.findAllById(compilationDto.getEvents())));
        }
        if (compilation.getTitle() != null) {
            compilation.setTitle(compilation.getTitle());
        }
        if (compilation.getPinned() != null) {
            compilation.setPinned(compilation.getPinned());
        }
        compilation = compilationRepository.save(compilation);
        return toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        return toDtoList(compilationRepository.findAllByPinned(pinned, pageable).getContent());
    }

    @Override
    public CompilationDto getById(Long compilationId) {
        return toDto(checkCompilation(compilationId));
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        checkCompilation(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private Compilation checkCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException(String.format("no such compilation id = %d",
                        compilationId))
        );
    }
}
