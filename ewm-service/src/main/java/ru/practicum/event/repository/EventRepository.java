package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.state.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllEventsByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findAllByCategoryId(Long catId);

    Set<Event> findAllByIdsIn(Set<Long> ids);

    @Query("select e from Event e " +
            "where e.initiator.id in ?1 " +
            "and e.state in ?2 " +
            "and e.category.id IN ?3 " +
            "and e.eventDate between ?4 and ?5")
    List<Event> adminFindEvents(List<Long> users, List<State> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event e " +
            "where e.state = ru.practicum.state.State.PUBLISHED " +
            "and (e.annotation like concat('%',?1,'%') OR e.description like concat('%',?1,'%')) " +
            "and e.category.id in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "and ((?6 = true and e.participantLimit = 0) " +
            "or (?6 = true and e.participantLimit > e.confirmedRequests) " +
            "or (?6 = false))")
    List<Event> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    @Query("select e from Event e " +
            "where e.state = ru.practicum.state.State.PUBLISHED " +
            "and e.category.id in ?1 " +
            "and e.paid = ?2 " +
            "and e.eventDate between ?3 and ?4 " +
            "and ((?5 = true and e.participantLimit = 0) " +
            "or (?5 = true and e.participantLimit > e.confirmedRequests) " +
            "or (?5 = false))")
    List<Event> findEventsWithoutText(List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);
}
