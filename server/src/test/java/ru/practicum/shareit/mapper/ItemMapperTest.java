package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для ItemMapper")
public class ItemMapperTest {

    private final User owner = new User(1L, "Owner", "owner@example.com");
    private final ItemRequest request = new ItemRequest(1L, "Request description", owner, null);

    @Test
    @DisplayName("Преобразование Item в ItemDto")
    void toItemDto_ShouldMapCorrectly() {
        Item item = new Item(10L, "Drill", "Electric drill", true, owner, request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(item.getRequest().getId(), dto.getRequestId());
        assertEquals(Collections.emptyList(), dto.getComments());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    @DisplayName("Преобразование ItemCreateDto в Item")
    void toItem_ShouldMapCorrectly() {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Laptop");
        createDto.setDescription("Gaming laptop");
        createDto.setAvailable(true);
        createDto.setRequestId(1L);

        Item item = ItemMapper.toItem(createDto, owner, request);

        assertNotNull(item);
        assertNull(item.getId());
        assertEquals(createDto.getName(), item.getName());
        assertEquals(createDto.getDescription(), item.getDescription());
        assertEquals(createDto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }
}