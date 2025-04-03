package ru.practicum.shareit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты UserController")
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private UserDto createTestUserDto(Long id) {
        return new UserDto(id, "User " + id, "user" + id + "@example.com");
    }

    private UserCreateDto createTestUserCreateDto() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        return dto;
    }

    private UserUpdateDto createTestUserUpdateDto() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated User");
        dto.setEmail("updated@example.com");
        return dto;
    }

    @Test
    @DisplayName("Создание пользователя - успешно")
    void createUser_ValidData_ReturnsCreated() {
        UserCreateDto createDto = createTestUserCreateDto();
        UserDto expectedDto = createTestUserDto(1L);

        doReturn(createTestUserDto(1L))
                .when(this.userService)
                .createUser(any(UserCreateDto.class));

        var result = this.userController.createUser(createDto);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.userService).createUser(createDto);
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Получение пользователя по ID - успешно")
    void getUser_ValidId_ReturnsUser() {
        long userId = 1L;
        UserDto expectedDto = createTestUserDto(userId);

        doReturn(createTestUserDto(1L))
                .when(this.userService)
                .getUser(userId);

        var result = this.userController.getUser(userId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.userService).getUser(userId);
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Получение пользователя - не найден")
    void getUser_NotFound_ThrowsException() {
        long userId = 999L;

        doThrow(new NotFoundException("User not found"))
                .when(this.userService)
                .getUser(999L);

        assertThrows(NotFoundException.class,
                () -> this.userController.getUser(userId));
        verify(this.userService).getUser(userId);
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Получение всех пользователей - успешно")
    void getAllUsers_ReturnsList() {
        doReturn(List.of(
                createTestUserDto(1L),
                createTestUserDto(2L)
        ))
                .when(this.userService)
                .getAllUsers();

        var result = this.userController.getAllUsers();

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        verify(this.userService).getAllUsers();
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Получение всех пользователей - пустой список")
    void getAllUsers_EmptyList_ReturnsOk() {
        doReturn(List.of())
                .when(this.userService)
                .getAllUsers();

        var result = this.userController.getAllUsers();

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertTrue(Objects.requireNonNull(result.getBody()).isEmpty());
        verify(this.userService).getAllUsers();
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Обновление пользователя - успешно")
    void updateUser_ValidData_ReturnsUpdated() {
        long userId = 1L;
        UserUpdateDto updateDto = createTestUserUpdateDto();
        UserDto expectedDto = createTestUserDto(userId);
        expectedDto.setName("Updated User");
        expectedDto.setEmail("updated@example.com");

        doReturn(expectedDto)
                .when(this.userService)
                .updateUser(1L, createTestUserUpdateDto());

        var result = this.userController.updateUser(userId, updateDto);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals("Updated User", Objects.requireNonNull(result.getBody()).getName());
        assertEquals("updated@example.com", result.getBody().getEmail());
        verify(this.userService).updateUser(userId, updateDto);
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Обновление пользователя - конфликт email")
    void updateUser_EmailConflict_ThrowsException() {
        long userId = 1L;
        UserUpdateDto updateDto = createTestUserUpdateDto();

        doThrow(new ConflictException("Email already exists"))
                .when(this.userService)
                .updateUser(1L, createTestUserUpdateDto());

        assertThrows(ConflictException.class,
                () -> this.userController.updateUser(userId, updateDto));
        verify(this.userService).updateUser(userId, updateDto);
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Удаление пользователя - успешно")
    void deleteUser_ValidId_ReturnsNoContent() {
        long userId = 1L;
        doNothing()
                .when(this.userService)
                .deleteUser(1L);

        var result = this.userController.deleteUser(userId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNull(result.getBody());
        verify(this.userService).deleteUser(userId);
        verifyNoMoreInteractions(this.userService);
    }

    @Test
    @DisplayName("Удаление пользователя - не найден")
    void deleteUser_NotFound_ThrowsException() {
        long userId = 999L;

        doThrow(new NotFoundException("User not found"))
                .when(this.userService)
                .deleteUser(999L);

        assertThrows(NotFoundException.class,
                () -> this.userController.deleteUser(userId));
        verify(this.userService).deleteUser(userId);
        verifyNoMoreInteractions(this.userService);
    }
}