package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.util.Headers.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> bookItem(
			@RequestHeader(USER_ID) long userId,
			@RequestBody @Valid BookingCreateDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(
			@RequestHeader(USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(
			@RequestHeader(USER_ID) long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsForOwner(
			@RequestHeader(USER_ID) Long ownerId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(defaultValue = "0") Integer from,
			@RequestParam(defaultValue = "10") Integer size) {

		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

		return bookingClient.getBookingsForOwner(ownerId, bookingState, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(
			@RequestHeader(USER_ID) long ownerId,
			@PathVariable Long bookingId,
			@RequestParam boolean approved) {
		log.info("Approve booking {}, ownerId={}, approved={}", bookingId, ownerId, approved);
		return bookingClient.approveBooking(bookingId, ownerId, approved);
	}

	@PatchMapping("/{bookingId}/cancel")
	public ResponseEntity<Object> cancelBooking(
			@RequestHeader(USER_ID) long userId,
			@PathVariable Long bookingId) {
		log.info("Cancel booking {}, userId={}", bookingId, userId);
		return bookingClient.cancelBooking(bookingId, userId);
	}
}