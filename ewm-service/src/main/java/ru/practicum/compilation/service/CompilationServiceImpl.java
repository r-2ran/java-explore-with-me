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
import java.util.Objects;

import static ru.practicum.compilation.CompilationMapper.*;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = to(compilationDto);
        if (compilationDto.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationDto.getEvents() == null) {
            compilation.setEvents(new HashSet<>());
        } else {
            compilation.setEvents(new HashSet<>(eventRepository.findAllByIdIn(compilationDto.getEvents())));
        }
        return toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = checkCompilation(compilationId);
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllByIdIn(compilationDto.getEvents())));
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilation = compilationRepository.save(compilation);
        return toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        if (Objects.equals(pinned, null)) {
            return toDtoList(compilationRepository.findAll(pageable).getContent());
        }
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
