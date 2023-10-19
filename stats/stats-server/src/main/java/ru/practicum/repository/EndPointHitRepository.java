package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndPointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndPointHit as h where h.timestamp between ?1 and ?2 and h.uri in ?3 " +
            "group by h.app, h.uri, h.ip order by count(distinct h.ip) desc", nativeQuery = true)
    List<ViewStats> getStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndPointHit h where h.timestamp between ?1 and ?2 and h.uri in ?3 " +
            "group by h.app, h.uri, h.ip order by count(h.ip) desc", nativeQuery = true)
    List<ViewStats> getStatsUnique(List<String> uris, LocalDateTime start, LocalDateTime end);
}
