package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserCreateDto dto) {
        return new User(null, dto.getName(), dto.getEmail());
    }
}