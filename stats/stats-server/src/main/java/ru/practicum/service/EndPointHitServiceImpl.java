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
import java.util.*;
import java.util.stream.Collectors;

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
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        List<EndpointHit> hits;
        List<ViewStats> viewStats = new ArrayList<>();

        if (uris == null) {
            hits = repository.findAllByTimestampBetween(startDate, endDate);
            for (EndpointHit hit : hits) {
                Long hitCount;
                if (isUnique) {
                    hitCount = repository.findHitCountWithUniqueIp();
                } else {
                    hitCount = repository.findHitCount();
                }
                viewStats.add(new ViewStats(hit.getApp(), hit.getUri(), hitCount));
            }
        } else {
            hits = repository.findAllByTimestampBetweenAndUriIn(startDate, endDate, uris);
            for (EndpointHit hit : hits) {
                Long hitCount;
                if (isUnique) {
                    hitCount = repository.findHitCountByUriWithUniqueIp(hit.getUri());
                } else {
                    hitCount = repository.findHitCountByUri(hit.getUri());
                }
                viewStats.add(new ViewStats(hit.getApp(), hit.getUri(), hitCount));
            }
        }
        viewStats = viewStats
                .stream()
                .sorted(Comparator.comparingLong(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
        if (!isUnique) {
            for (int i = 0; i < viewStats.size() - 1; i++) {
                if (viewStats.get(i).getUri().equals(viewStats.get(i + 1).getUri())) {
                    viewStats.get(i + 1).setHits(viewStats.get(i + 1).getHits() +
                            viewStats.get(i).getHits());
                    viewStats.remove(i);
                }
            }
        }
        return toDtos(viewStats);
    }
}
