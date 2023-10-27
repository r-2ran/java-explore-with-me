package ru.practicum.location.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;

import static ru.practicum.location.mapper.LocationMapper.*;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;

    @Override
    public void deleteById(Long locationId) {
        if (repository.existsById(locationId)) {
            repository.deleteById(locationId);
        } else {
            throw new NotFoundException(String.format("no such location id %d", locationId));
        }
    }

    @Override
    public Location addLocation(LocationDto locationDto) {
        return repository.save(toLocation(locationDto));
    }

    @Override
    public Location getByParam(Float lon, Float lat) {
        return repository.findByLonAndLat(lon, lat).orElseThrow(
                () -> new NotFoundException("no such location found")
        );
    }
}
