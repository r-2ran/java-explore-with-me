package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilation.mapper.CompilationMapper.*;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = Compilation.builder()
                .events(compilationDto.getEvents() == null ? new ArrayList<>() : eventRepository.findAllByIdIn(compilationDto.getEvents()))
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned() != null && compilationDto.getPinned())
                .build();
        return toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = checkCompilation(compilationId);
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(compilationDto.getEvents()));
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        return toDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
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
