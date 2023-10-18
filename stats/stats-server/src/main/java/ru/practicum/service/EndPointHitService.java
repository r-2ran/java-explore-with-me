package ru.practicum.service;


import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface EndPointHitService {
    void addHit(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(List<String> uris, boolean isUnique, String start, String end);
}
