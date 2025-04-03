package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для ItemRequestService")
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setup() {
        user = new User(1L, "User", "user@example.com");
        request = new ItemRequest(1L, "Request description", user, LocalDateTime.now());
        item = new Item(1L, "Item", "Desc", true, user, request);
    }

    @Test
    @DisplayName("Создание запроса - успешно")
    void createRequest_validInput_success() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a laptop");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestService.createRequest(dto, 1L);

        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("Создание запроса - пустое описание")
    void createRequest_blankDescription_throws() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("   ");

        assertThrows(ValidationException.class, () -> itemRequestService.createRequest(dto, 1L));
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    @DisplayName("Создание запроса - пользователь не найден")
    void createRequest_userNotFound_throws() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need something");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(dto, 1L));
    }

    @Test
    @DisplayName("Создание запроса - null в описании")
    void createRequest_nullDescription_throws() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(null);

        assertThrows(ValidationException.class, () -> itemRequestService.createRequest(dto, 1L));
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    @DisplayName("Получение всех запросов пользователя")
    void getUserRequests_returnsList() {
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequest(request)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Получение всех запросов пользователя - пустой список")
    void getUserRequests_empty_returnsEmptyList() {
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Получение всех запросов - кроме пользователя")
    void getAllRequests_excludingUser_returnsList() {
        when(itemRequestRepository.findAllExcludingUser(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequest(request)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);

        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Получение всех запросов - кроме пользователя - пустой список")
    void getAllRequests_onlyUserRequests_returnsEmptyList() {
        when(itemRequestRepository.findAllExcludingUser(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Получение запроса по id - у запроса нет связанных предметов")
    void getRequestById_noItems_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(request)).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(0, result.getItems().size());
    }

    @Test
    @DisplayName("Получение запроса по id - успешно")
    void getRequestById_validId_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(request)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertEquals(request.getId(), result.getId());
        assertEquals("Request description", result.getDescription());
    }

    @Test
    @DisplayName("Получение запроса по id - пользователь не найден")
    void getRequestById_userNotFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }

    @Test
    @DisplayName("Получение запроса по id - запрос не найден")
    void getRequestById_requestNotFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }
}