package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndPointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndPointHit h where h.timestamp between ?1 and ?2 and h.uri in ?3 " +
            "group by h.app, h.uri, h.ip order by count(distinct h.ip) desc")
    List<ViewStats> getStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndPointHit h where h.timestamp between ?1 and ?2 and h.uri in ?3 " +
            "group by h.app, h.uri, h.ip order by count(h.ip) desc")
    List<ViewStats> getStatsUnique(List<String> uris, LocalDateTime start, LocalDateTime end);
}
