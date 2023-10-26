package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<UserDto> findByIdIn(List<Long> ids, Pageable pageable);

    List<UserDto> findByIdIn(List<Long> ids);

}
