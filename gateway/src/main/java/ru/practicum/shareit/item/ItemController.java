package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.util.Headers.USER_ID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(USER_ID) Long ownerId,
            @Valid @RequestBody ItemCreateDto itemDto) {
        log.info("Creating item {}, ownerId={}", itemDto, ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, ownerId={}", itemId, ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long itemId) {
        log.info("Getting item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader(USER_ID) Long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Getting items of user {}, from={}, size={}", ownerId, from, size);
        return itemClient.getItems(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentDto) {
        log.info("Adding comment to item {}, userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}