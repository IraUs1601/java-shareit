package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

import static ru.practicum.shareit.util.Headers.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(USER_ID) Long userId,
            @RequestBody ItemCreateDto dto) {
        return ResponseEntity.ok(itemService.createItem(dto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentCreateDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(
            @RequestHeader(USER_ID) Long ownerId) {
        return ResponseEntity.ok(itemService.getItems(ownerId));
    }

    @GetMapping("/user-items")
    public ResponseEntity<List<ItemDto>> getUserItems(
            @RequestParam(value = "owner", required = false) String ownerIdStr) {
        Long ownerId = null;
        if (ownerIdStr != null) {
            try {
                ownerId = Long.parseLong(ownerIdStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid ownerId: must be a number");
            }
        }
        return ResponseEntity.ok(itemService.getItems(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDto dto) {
        return ResponseEntity.ok(itemService.updateItem(itemId, dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long id) {
        itemService.deleteItem(id, userId);
        return ResponseEntity.noContent().build();
    }
}