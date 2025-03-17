package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import java.util.Collections;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                (item.getRequest() != null ? item.getRequest().getId() : null),
                null,
                null,
                Collections.emptyList()
        );
    }

    public static Item toItem(ItemCreateDto dto, User owner, ItemRequest request) {
        return new Item(
                null,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable() != null ? dto.getAvailable() : false,
                owner,
                request
        );
    }
}