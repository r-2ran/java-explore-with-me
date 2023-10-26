package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;

import java.util.ArrayList;
import java.util.List;

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

    public static Compilation to(NewCompilationDto compilationDto) {
        return new Compilation(
                compilationDto.getTitle(),
                compilationDto.getPinned()
        );
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilations) {
        List<CompilationDto> res = new ArrayList<>();
        for (Compilation compilation : compilations) {
            res.add(toDto(compilation));
        }
        return res;
    }
}
