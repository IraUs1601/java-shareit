package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для ItemRequestMapper")
public class ItemRequestMapperTest {

    @Test
    @DisplayName("Преобразование ItemRequest в ItemRequestDto")
    void toItemRequestDto_ShouldMapCorrectly() {
        ItemRequest request = new ItemRequest(1L, "Нужна дрель", new User(1L, "Иван", "ivan@example.com"), LocalDateTime.now());
        Item item = new Item(10L, "Дрель", "Мощная дрель", true, request.getRequestor(), request);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, List.of(item));

        assertNotNull(dto);
        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
        assertEquals(1, dto.getItems().size());
        assertEquals(item.getId(), dto.getItems().get(0).getId());
    }

    @Test
    @DisplayName("Преобразование ItemRequestCreateDto в ItemRequest")
    void toItemRequest_ShouldMapCorrectly() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Хочу ноутбук");
        User user = new User(1L, "Иван", "ivan@example.com");

        ItemRequest result = ItemRequestMapper.toItemRequest(createDto, user);

        assertNotNull(result);
        assertEquals(createDto.getDescription(), result.getDescription());
        assertEquals(user, result.getRequestor());
        assertNotNull(result.getCreated());
    }
}