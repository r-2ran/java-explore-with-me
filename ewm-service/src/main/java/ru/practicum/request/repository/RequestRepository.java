package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long id);

    List<Request> findAllByEventId(Long id);

    List<Request> findAllByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findAllByIdAndStatusEquals(Long requestId, String state);
}
