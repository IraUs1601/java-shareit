package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateDto {
    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;
}