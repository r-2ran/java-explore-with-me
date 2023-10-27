package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.location.model.Location;
import ru.practicum.request.model.Request;
import ru.practicum.state.State;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "annotation", nullable = false)
    String annotation;
    @Column(name = "description", nullable = false)
    String description;
    @Column(name = "title", nullable = false)
    String title;
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    State state;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    Location location;
    @Column(name = "created", nullable = false)
    LocalDateTime created;
    @Column(name = "eventDate", nullable = false)
    LocalDateTime eventDate;
    @Column(name = "published_on", nullable = false)
    LocalDateTime publishedOn;
    @Column(name = "paid", nullable = false)
    Boolean paid;
    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration;
    @Column(name = "participant_limit", nullable = false)
    Integer participantLimit;
    @OneToMany(mappedBy = "event")
    List<Request> confirmedRequests;
    @Column(name = "views", columnDefinition = "int default 0")
    Integer views;

    public Event(String annotation, String description, String title, LocalDateTime eventDate,
                 Location location, Boolean paid, Integer participantLimit, Boolean requestModeration) {
        this.annotation = annotation;
        this.description = description;
        this.title = title;
        this.eventDate = eventDate;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
    }
}
