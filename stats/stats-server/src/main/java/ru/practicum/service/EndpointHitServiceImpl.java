package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.practicum.mapper.EndpointHitMapper.*;
import static ru.practicum.mapper.ViewStatsMapper.*;

@Service
@AllArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository repository;

    @Override
    @Transactional
    public void addHit(EndpointHitDto hitDto) {
        repository.save(to(hitDto));
    }

    @Override
    @Transactional
    public List<ViewStatsDto> getStats(List<String> uris, boolean isUnique,
                                       String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        List<ViewStats> res;

        if (isUnique) {
            if (uris == null) {
                res = repository.findAllUniqueIp(startDate, endDate);
            } else {
                res = repository.findUniqueIpByUris(startDate, endDate, uris);
            }
        } else {
            if (uris == null) {
                res = repository.findAllNotUniqueIp(startDate, endDate);
            } else {
                res = repository.findNotUniqueIpByUris(startDate, endDate, uris);
            }
        }
        return toDtos(res);
    }
}
