package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto createBooking(BookingCreateDto dto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking.");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new UnauthorizedException("Owner cannot book their own item.");
        }

        Booking booking = BookingMapper.toBooking(dto, booker, item);
        booking.setCreated(LocalDateTime.now());

        booking = bookingRepository.save(booking);
        bookingRepository.flush();

        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this booking.");
        }

        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getBookings(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookings(userId, now);
                break;
            case "PAST":
                bookings = bookingRepository.findPastBookings(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureBookings(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByStatusAndBookerIdOrderByStartDesc(Booking.BookingStatus.WAITING, userId);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByStatusAndBookerIdOrderByStartDesc(Booking.BookingStatus.REJECTED, userId);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("Only the owner can approve or reject a booking.");
        }

        if (!booking.getStatus().equals(Booking.BookingStatus.WAITING)) {
            throw new ValidationException("Booking is already processed.");
        }

        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        if (!booking.getBooker().getId().equals(userId)) {
            throw new UnauthorizedException("Only the booker can cancel the booking.");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot cancel an already started booking.");
        }

        booking.setStatus(Booking.BookingStatus.CANCELED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}