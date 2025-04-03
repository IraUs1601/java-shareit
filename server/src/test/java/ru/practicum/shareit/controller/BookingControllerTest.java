package ru.practicum.shareit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты BookingController")
public class BookingControllerTest {
    @Mock
    BookingService bookingService;
    @InjectMocks
    BookingController bookingController;

    private BookingDto createTestBookingDto(Long id) {
        return new BookingDto(
                id,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                Booking.BookingStatus.WAITING
        );
    }

    @Test
    @DisplayName("Cоздание нового booking")
    void createBooking_RequestIsValid_ReturnOK() {
        long id = 1L;
        var payload = new BookingCreateDto();
        payload.setItemId(1L);
        payload.setStart(LocalDateTime.now().plusDays(1));
        payload.setEnd(LocalDateTime.now().plusDays(2));

        doReturn(createTestBookingDto(1L))
                .when(this.bookingService)
                .createBooking(notNull(), anyLong());

        var result = this.bookingController.createBooking(id, payload);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(1L, Objects.requireNonNull(result.getBody()).getId());
        assertTrue(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS)
                .isEqual(result.getBody().getStart().truncatedTo(ChronoUnit.DAYS)));
        assertTrue(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS)
                .isEqual(result.getBody().getStart().truncatedTo(ChronoUnit.HOURS)));

        verify(this.bookingService).createBooking(payload, id);
        verifyNoMoreInteractions(this.bookingService);

    }

    @Test
    @DisplayName("Получение бронирования по ID - успешно")
    void getBooking_ValidRequest_ReturnBooking() {
        long userId = 1L;
        long bookingId = 1L;

        doReturn(createTestBookingDto(1L))
                .when(this.bookingService)
                .getBooking(bookingId, userId);

        var result = this.bookingController.getBooking(userId, bookingId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertTrue(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS)
                .isEqual(result.getBody().getStart().truncatedTo(ChronoUnit.DAYS)));
        assertTrue(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS)
                .isEqual(result.getBody().getStart().truncatedTo(ChronoUnit.HOURS)));
        verify(this.bookingService).getBooking(bookingId, userId);
        verifyNoMoreInteractions(this.bookingService);
    }

    @Test
    @DisplayName("Получение списка бронирований пользователя - все состояния")
    void getBookings_AllStates_ReturnList() {
        long userId = 1L;
        String state = "ALL";
        List<BookingDto> expectedList = List.of(createTestBookingDto(1L), createTestBookingDto(2L));

        doReturn(List.of(createTestBookingDto(1L), createTestBookingDto(2L)))
                .when(this.bookingService)
                .getBookings(userId, state);

        var result = bookingController.getBookings(userId, state);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedList, result.getBody());
        verify(this.bookingService).getBookings(userId, state);
        verifyNoMoreInteractions(this.bookingService);
    }

    @Test
    @DisplayName("Подтверждение бронирования - успешно")
    void approveBooking_ValidRequest_ReturnApproved() {
        long ownerId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        BookingDto expectedDto = createTestBookingDto(bookingId);
        expectedDto.setStatus(Booking.BookingStatus.APPROVED);

        doReturn(expectedDto)
                .when(this.bookingService)
                .approveBooking(1L, 1L, true);

        var result = this.bookingController.approveBooking(ownerId, bookingId, approved);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(Booking.BookingStatus.APPROVED, Objects.requireNonNull(result.getBody()).getStatus());
        verify(this.bookingService).approveBooking(bookingId, ownerId, approved);
        verifyNoMoreInteractions(this.bookingService);
    }

    @Test
    @DisplayName("Отклонение бронирования - успешно")
    void approveBooking_RejectRequest_ReturnRejected() {
        long ownerId = 1L;
        long bookingId = 1L;
        boolean approved = false;
        BookingDto expectedDto = createTestBookingDto(bookingId);
        expectedDto.setStatus(Booking.BookingStatus.REJECTED);

        doReturn(expectedDto)
                .when(this.bookingService)
                .approveBooking(1L, 1L, false);

        var result = this.bookingController.approveBooking(ownerId, bookingId, approved);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(Booking.BookingStatus.REJECTED, Objects.requireNonNull(result.getBody()).getStatus());
        verify(this.bookingService).approveBooking(bookingId, ownerId, approved);
        verifyNoMoreInteractions(this.bookingService);
    }

    @Test
    @DisplayName("Отмена бронирования - успешно")
    void cancelBooking_ValidRequest_ReturnCanceled() {
        long userId = 1L;
        long bookingId = 1L;
        BookingDto expectedDto = createTestBookingDto(bookingId);
        expectedDto.setStatus(Booking.BookingStatus.CANCELED);

        doReturn(expectedDto)
                .when(this.bookingService)
                .cancelBooking(1L, 1L);

        var result = this.bookingController.cancelBooking(userId, bookingId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(Booking.BookingStatus.CANCELED, Objects.requireNonNull(result.getBody()).getStatus());
        verify(this.bookingService).cancelBooking(bookingId, userId);
        verifyNoMoreInteractions(this.bookingService);
    }

    @Test
    @DisplayName("Получение бронирования - не найден")
    void getBooking_NotFound_ThrowsException() {
        long userId = 1L;
        long bookingId = 999L;

        doThrow(new NotFoundException("Booking not found"))
                .when(this.bookingService)
                .getBooking(999L, 1L);

        assertThrows(NotFoundException.class,
                () -> this.bookingController.getBooking(userId, bookingId));
        verify(this.bookingService).getBooking(bookingId, userId);
        verifyNoMoreInteractions(this.bookingService);
    }

    @Test
    @DisplayName("Получение бронирования - неавторизованный доступ")
    void getBooking_Unauthorized_ThrowsException() {
        long userId = 2L;
        long bookingId = 1L;

        doThrow(new UnauthorizedException("Access denied"))
                .when(this.bookingService)
                .getBooking(1L, 2L);

        assertThrows(UnauthorizedException.class,
                () -> this.bookingController.getBooking(userId, bookingId));
    }

    @Test
    @DisplayName("Получение бронирований - пустой список")
    void getBookings_EmptyList_ReturnsOk() {
        long userId = 1L;
        String state = "ALL";

        doReturn(List.of())
                .when(this.bookingService)
                .getBookings(1L, "ALL");

        var result = this.bookingController.getBookings(userId, "ALL");

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertTrue(Objects.requireNonNull(result.getBody()).isEmpty());
        verify(this.bookingService).getBookings(userId, state);
        verifyNoMoreInteractions(this.bookingService);

    }

    @Test
    @DisplayName("Получение бронирований для владельца - все состояния")
    void getBookingsForOwner_ReturnList() {
        long ownerId = 1L;
        String state = "ALL";
        List<BookingDto> expectedList = List.of(createTestBookingDto(1L));

        doReturn(expectedList)
                .when(bookingService)
                .getBookingsForOwner(ownerId, state);

        var result = bookingController.getBookingsForOwner(ownerId, state);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedList, result.getBody());
        verify(bookingService).getBookingsForOwner(ownerId, state);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @DisplayName("Подтверждение бронирования - повторное подтверждение")
    void approveBooking_AlreadyApproved_ThrowsException() {
        long ownerId = 1L;
        long bookingId = 1L;

        doThrow(new ValidationException("Already approved"))
                .when(this.bookingService)
                .approveBooking(1L, 1L, true);

        assertThrows(ValidationException.class,
                () -> bookingController.approveBooking(ownerId, bookingId, true));
    }


    @Test
    @DisplayName("Отмена бронирования - слишком поздно")
    void cancelBooking_TooLate_ThrowsException() {
        long userId = 1L;
        long bookingId = 1L;

        doThrow(new ValidationException("Booking already started"))
                .when(this.bookingService)
                .cancelBooking(1L, 1L);
        assertThrows(ValidationException.class,
                () -> bookingController.cancelBooking(userId, bookingId));
    }
}