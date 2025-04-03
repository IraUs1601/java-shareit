package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        List<ItemDto> itemDtos = items.stream()
                .map(item -> new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), request.getId()))
                .collect(Collectors.toList());

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos
        );
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto dto, User requestor) {
        return new ItemRequest(
                null,
                dto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
    }
}