package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.state.State;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Column
    String annotation;
    @Column
    String description;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @Column
    private String title;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;
    @Column
    Float lat;
    @Column
    Float lon;
    @Column
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    State state;

    public Event(String annotation, String description, String title, LocalDateTime eventDate,
                 Float lat, Float lon, Boolean paid, Integer participantLimit, Boolean requestModeration) {
        this.annotation = annotation;
        this.description = description;
        this.title = title;
        this.eventDate = eventDate;
        this.lat = lat;
        this.lon = lon;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
    }
}
