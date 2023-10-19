package ru.practicum.service;


import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;

@Service
public interface EndPointHitService {
    void addHit(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(List<String> uris, boolean isUnique, String start, String end);
}
