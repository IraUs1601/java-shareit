package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item toItem(ItemCreateDto dto, Long ownerId) {
        return new Item(
                null,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                ownerId
        );
    }
}