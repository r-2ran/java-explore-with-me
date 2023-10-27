package ru.practicum.location.service;

import org.springframework.stereotype.Service;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

@Service
public interface LocationService {
    void deleteById(Long locationId);

    Location addLocation(LocationDto locationDto);

    Location getByParam(Float lon, Float lat);
}
