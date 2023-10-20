package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndPointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri, h.ip " +
            "order by count(distinct h.ip) desc")
    List<ViewStats> findAllUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(h.app)) " +
            "from EndpointHit h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.app) desc")
    List<ViewStats> findAllNotUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h " +
            "where h.timestamp between ?1 and ?2 and h.uri in ?3 " +
            "group by h.app, h.uri, h.ip " +
            "order by count(distinct h.ip) desc")
    List<ViewStats> findUniqueIpByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(h.app)) " +
            "from EndpointHit h " +
            "where h.timestamp between ?1 and ?2 and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(h.app) desc")
    List<ViewStats> findNotUniqueIpByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
