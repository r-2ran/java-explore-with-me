package ru.practicum.location.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JoinColumn(name = "lat")
    Float lat;
    @JoinColumn(name = "lon")
    Float lon;

    public Location(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
