package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static ru.practicum.shareit.util.Headers.USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader(USER_ID) Long userId,
            @Valid @RequestBody BookingCreateDto dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookings(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getBookings(userId, state));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader(USER_ID) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, ownerId, approved));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }
}