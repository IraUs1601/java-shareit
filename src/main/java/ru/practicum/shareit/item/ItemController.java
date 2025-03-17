package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemCreateDto dto) {
        return ResponseEntity.ok(itemService.createItem(dto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(
            @RequestParam(value = "owner", required = false) Long ownerId) {
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ownerId: must be a number");
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
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDto dto) {
        return ResponseEntity.ok(itemService.updateItem(itemId, dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {
        itemService.deleteItem(id, userId);
        return ResponseEntity.noContent().build();
    }
}