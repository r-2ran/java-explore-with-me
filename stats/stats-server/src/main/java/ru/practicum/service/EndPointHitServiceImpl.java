package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.EndPointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.mapper.EndPointHitMapper.*;
import static ru.practicum.mapper.ViewStatsMapper.*;

@Service
@AllArgsConstructor
public class EndPointHitServiceImpl implements EndPointHitService {
    private final EndPointHitRepository repository;

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
        List<EndpointHit> hits = repository.findAllByTimestampBetweenAndUriIn(LocalDateTime
                .parse(start, formatter), LocalDateTime.parse(end, formatter), uris);
        List<ViewStats> viewStats = new ArrayList<>();

        for (EndpointHit hit : hits) {
            Long hitCount;
            if (isUnique) {
                hitCount = repository.findHitCountByUriWithUniqueIp(hit.getUri());
            } else {
                hitCount = repository.findHitCountByUri(hit.getUri());
            }
            viewStats.add(new ViewStats(hit.getApp(), hit.getUri(), hitCount));
        }
        return toDtos(viewStats);
    }
}
