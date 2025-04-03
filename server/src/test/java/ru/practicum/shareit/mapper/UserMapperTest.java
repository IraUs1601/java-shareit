package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для UserMapper")
public class UserMapperTest {

    @Test
    @DisplayName("toUserDto корректно преобразует User в UserDto")
    void toUserDto_ShouldMapCorrectly() {
        User user = new User(1L, "Иван", "ivan@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    @DisplayName("toUser корректно преобразует UserCreateDto в User")
    void toUser_ShouldMapCorrectly() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Иван");
        dto.setEmail("ivan@example.com");

        User user = UserMapper.toUser(dto);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }
}