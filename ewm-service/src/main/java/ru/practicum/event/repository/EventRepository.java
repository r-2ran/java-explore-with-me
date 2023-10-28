package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllEventsByInitiatorId(Long initiatorId, Pageable pageable);

    List<Event> findAllByCategoryId(Long catId);

    List<Event> findAllByIdIn(Set<Long> ids);

    @Query("select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%'))")
    List<Event> findAllByText(String text, Specification<Event> specification, Pageable pageable);
}
