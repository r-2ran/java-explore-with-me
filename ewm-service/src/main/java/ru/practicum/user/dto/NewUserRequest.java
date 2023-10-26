package ru.practicum.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    String email;
    @Size(min = 2, max = 250)
    @NotBlank
    String name;

}
