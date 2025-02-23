package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(ItemCreateDto dto, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found.");
        }
        Item item = ItemMapper.toItem(dto, userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.findAllByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) return List.of();
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto updateItem(Long itemId, ItemUpdateDto dto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("Only owner can update the item");
        }

        Optional.ofNullable(dto.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(item::setName);

        Optional.ofNullable(dto.getDescription())
                .filter(desc -> !desc.isBlank())
                .ifPresent(item::setDescription);

        Optional.ofNullable(dto.getAvailable()).ifPresent(item::setAvailable);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public void deleteItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("Only owner can delete the item");
        }

        itemRepository.delete(itemId);
    }
}