package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.Headers.USER_ID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader(USER_ID) Long userId,
            @Valid @RequestBody ItemRequestCreateDto dto) {
        return ResponseEntity.ok(itemRequestService.createRequest(dto, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(
            @RequestHeader(USER_ID) Long userId) {
        return ResponseEntity.ok(itemRequestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader(USER_ID) Long userId) {
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId));
    }

    @GetMapping("/{requestId}/items")
    public ResponseEntity<List<ItemDto>> getItemsByRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getItemsByRequest(requestId));
    }
}