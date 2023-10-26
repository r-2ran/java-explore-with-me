package ru.practicum.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String title;
    @Column
    Boolean pinned;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "events_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @ToString.Exclude
    Set<Event> events;

    public Compilation(String title, Boolean pinned) {
        this.title = title;
        this.pinned = pinned;
    }

}
