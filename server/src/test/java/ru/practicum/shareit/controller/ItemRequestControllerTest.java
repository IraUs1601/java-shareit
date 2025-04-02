package ru.practicum.shareit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ItemRequestController")
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;

    private ItemRequestDto createTestItemRequestDto(Long id) {
        return new ItemRequestDto(
                id,
                "Description " + id,
                LocalDateTime.now(),
                List.of()
        );
    }

    private ItemRequestCreateDto createTestItemRequestCreateDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Test description");
        return dto;
    }

    private ItemDto createTestItemDto(Long id) {
        return new ItemDto(
                id,
                "Item " + id,
                "Description " + id,
                true,
                id
        );
    }

    @Test
    @DisplayName("Создание запроса - успешно")
    void createRequest_ValidData_ReturnsCreated() {
        long userId = 1L;
        ItemRequestCreateDto createDto = createTestItemRequestCreateDto();
        ItemRequestDto expectedDto = createTestItemRequestDto(1L);

        doReturn(expectedDto)
                .when(this.itemRequestService)
                .createRequest(any(ItemRequestCreateDto.class), anyLong());

        var result = this.itemRequestController.createRequest(userId, createDto);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.itemRequestService).createRequest(createDto, userId);
        verifyNoMoreInteractions(this.itemRequestService);
    }

    @Test
    @DisplayName("Получение запроса по ID - успешно")
    void getRequestById_ValidRequest_ReturnsRequest() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDto expectedDto = createTestItemRequestDto(requestId);

        doReturn(expectedDto)
                .when(this.itemRequestService)
                .getRequestById(userId, requestId);

        var result = this.itemRequestController.getRequestById(userId, requestId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.itemRequestService).getRequestById(userId, requestId);
        verifyNoMoreInteractions(this.itemRequestService);
    }

    @Test
    @DisplayName("Получение запроса по ID - не найден")
    void getRequestById_NotFound_ThrowsException() {
        long userId = 1L;
        long requestId = 999L;

        doThrow(new NotFoundException("Request not found"))
                .when(this.itemRequestService)
                .getRequestById(userId, requestId);

        assertThrows(NotFoundException.class,
                () -> this.itemRequestController.getRequestById(userId, requestId));
        verify(this.itemRequestService).getRequestById(userId, requestId);
        verifyNoMoreInteractions(this.itemRequestService);
    }

    @Test
    @DisplayName("Получение запросов пользователя - успешно")
    void getUserRequests_ValidUser_ReturnsList() {
        long userId = 1L;
        List<ItemRequestDto> expectedList = List.of(
                createTestItemRequestDto(1L),
                createTestItemRequestDto(2L)
        );

        doReturn(expectedList)
                .when(this.itemRequestService)
                .getUserRequests(userId);

        var result = this.itemRequestController.getUserRequests(userId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        verify(this.itemRequestService).getUserRequests(userId);
        verifyNoMoreInteractions(this.itemRequestService);
    }

    @Test
    @DisplayName("Получение запросов пользователя - пустой список")
    void getUserRequests_NoRequests_ReturnsEmptyList() {
        long userId = 1L;

        doReturn(List.of())
                .when(this.itemRequestService)
                .getUserRequests(userId);

        var result = this.itemRequestController.getUserRequests(userId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertTrue(Objects.requireNonNull(result.getBody()).isEmpty());
        verify(this.itemRequestService).getUserRequests(userId);
        verifyNoMoreInteractions(this.itemRequestService);
    }

    @Test
    @DisplayName("Получение всех запросов - успешно")
    void getAllRequests_ReturnsList() {
        long userId = 1L;
        List<ItemRequestDto> expectedList = List.of(
                createTestItemRequestDto(1L),
                createTestItemRequestDto(2L)
        );

        doReturn(expectedList)
                .when(this.itemRequestService)
                .getAllRequests(userId);

        var result = this.itemRequestController.getAllRequests(userId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        verify(this.itemRequestService).getAllRequests(userId);
        verifyNoMoreInteractions(this.itemRequestService);
    }

    @Test
    @DisplayName("Получение всех запросов - пустой список")
    void getAllRequests_NoRequests_ReturnsEmptyList() {
        long userId = 1L;

        doReturn(List.of())
                .when(this.itemRequestService)
                .getAllRequests(userId);

        var result = this.itemRequestController.getAllRequests(userId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertTrue(Objects.requireNonNull(result.getBody()).isEmpty());
        verify(this.itemRequestService).getAllRequests(userId);
        verifyNoMoreInteractions(this.itemRequestService);
    }
}