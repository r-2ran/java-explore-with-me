package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndPointHitRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findAllByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT COUNT (ip) FROM Hit " +
            "WHERE uri = ?1")
    Long findHitCountByUri(String uri);

    @Query("SELECT COUNT (DISTINCT ip) FROM Hit " +
            "WHERE uri = ?1")
    Long findHitCountByUriWithUniqueIp(String uri);
}
