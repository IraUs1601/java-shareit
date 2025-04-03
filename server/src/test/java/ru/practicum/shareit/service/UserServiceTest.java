package ru.practicum.shareit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя - успешно")
    void createUser_validDto_success() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("John");
        dto.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserDto result = userService.createUser(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Создание пользователя - дублирующий email")
    void createUser_duplicateEmail_throwsConflict() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class, () -> userService.createUser(dto));
    }

    @DisplayName("Создание пользователя — email пустой")
    @Test
    void createUser_blankEmail_throwsValidation() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("   ");
        assertThrows(ValidationException.class, () -> userService.createUser(dto));
    }

    @DisplayName("Создание пользователя — email null")
    @Test
    void createUser_nullEmail_throwsValidation() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail(null);
        assertThrows(ValidationException.class, () -> userService.createUser(dto));
    }

    @DisplayName("Создание пользователя — email без '@'")
    @Test
    void createUser_invalidEmailFormat_throwsValidation() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("invalidEmail.com");
        assertThrows(ValidationException.class, () -> userService.createUser(dto));
    }

    @Test
    @DisplayName("Обновление пользователя - успешно")
    void updateUser_validData_success() {
        Long userId = 1L;
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("new@example.com");
        dto.setName("New Name");

        User user = new User(userId, "Old Name", "old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.updateUser(userId, dto);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Обновление пользователя - конфликт email")
    void updateUser_conflictEmail_throwsConflict() {
        Long userId = 1L;
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("existing@example.com");

        User existing = new User(2L, "Other", "existing@example.com");
        User current = new User(userId, "Me", "me@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(current));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> userService.updateUser(userId, dto));
    }

    @DisplayName("Обновление пользователя — без изменений")
    @Test
    void updateUser_noChanges_returnsSameUser() {
        Long userId = 1L;
        UserUpdateDto dto = new UserUpdateDto();

        User user = new User(userId, "Name", "email@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.updateUser(userId, dto);

        assertThat(result.getEmail()).isEqualTo("email@example.com");
        assertThat(result.getName()).isEqualTo("Name");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Получение пользователя по ID - найден")
    void getUser_found_success() {
        User user = new User(1L, "User", "user@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Получение пользователя по ID - не найден")
    void getUser_notFound_throwsNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "User1", "u1@example.com"),
                new User(2L, "User2", "u2@example.com")
        ));

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Получение всех пользователей — пустой список")
    void getAllUsers_emptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_success() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}