package ru.practicum.service;


import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EndpointHitService {
    void addHit(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(List<String> uris, boolean isUnique, LocalDateTime start, LocalDateTime end);
}
