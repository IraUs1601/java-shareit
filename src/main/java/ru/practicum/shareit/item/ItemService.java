package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemCreateDto dto, Long userId);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long ownerId);

    List<ItemDto> searchItems(String text);

    ItemDto updateItem(Long itemId, ItemUpdateDto dto, Long userId);

    void deleteItem(Long itemId, Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);
}