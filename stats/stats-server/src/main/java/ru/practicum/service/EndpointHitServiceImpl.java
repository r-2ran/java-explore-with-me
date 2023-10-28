package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.WrongDateException;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
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
                                       LocalDateTime start, LocalDateTime end) {
        List<ViewStats> res;
        if (start == null || end == null) {
            throw new WrongDateException("don't have required param start date oe end date");
        }
        if (start.isAfter(end)) {
            throw new WrongDateException("start cannot be after end");
        }
        if (isUnique) {
            if (uris == null) {
                res = repository.findAllUniqueIp(start, end);
            } else {
                res = repository.findUniqueIpByUris(start, end, uris);
            }
        } else {
            if (uris == null) {
                res = repository.findAllNotUniqueIp(start, end);
            } else {
                res = repository.findNotUniqueIpByUris(start, end, uris);
            }
        }
        return toDtos(res);
    }
}
