package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;


import static ru.practicum.event.mapper.EventMapper.*;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {


        return new CompilationDto(
                toShortEventDtoSet(compilation.getEvents()),
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
