package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты BookingService")
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingService bookingService;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@example.com");
        owner = new User(2L, "Owner", "owner@example.com");
        item = new Item(1L, "Item", "Desc", true, owner, null);
    }

    @Test
    @DisplayName("Создание бронирования - успешно")
    void createBooking_success() {
        BookingCreateDto dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.createBooking(dto, user.getId());

        assertNotNull(result);
        assertEquals(dto.getStart().toLocalDate(), result.getStart().toLocalDate());
        verify(bookingRepository).save(any());
    }

    @Test
    @DisplayName("Создание бронирования - бронирование недоступного предмета")
    void createBooking_itemNotAvailable() {
        item.setAvailable(false);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(dto, user.getId()));
    }

    @Test
    @DisplayName("Создание бронирования — попытка забронировать свой предмет")
    void createBooking_ownItem() {
        item.setOwner(user);
        BookingCreateDto dto = new BookingCreateDto(item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(UnauthorizedException.class, () -> bookingService.createBooking(dto, user.getId()));
    }

    @Test
    @DisplayName("Получение бронирования - успех")
    void getBooking_success() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.BookingStatus.WAITING)
                .created(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(1L, user.getId());

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Получение бронирования - неавторизованный доступ")
    void getBooking_unauthorized() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(new User(99L, "Other", "o@x.com"))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.BookingStatus.WAITING)
                .created(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedException.class, () -> bookingService.getBooking(1L, user.getId()));
    }

    @ParameterizedTest
    @DisplayName("Получение бронирований пользователя по состоянию")
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "UNKNOWN"})
    void getBookings_byState(String state) {
        long userId = user.getId();
        when(userRepository.existsById(userId)).thenReturn(true);
        List<Booking> mockBookings = List.of(
                Booking.builder().id(1L).booker(user).item(item).start(LocalDateTime.now().plusDays(1)).build()
        );

        switch (state.toUpperCase()) {
            case "CURRENT" -> when(bookingRepository.findCurrentBookings(eq(userId), any())).thenReturn(mockBookings);
            case "PAST" -> when(bookingRepository.findPastBookings(eq(userId), any())).thenReturn(mockBookings);
            case "FUTURE" -> when(bookingRepository.findFutureBookings(eq(userId), any())).thenReturn(mockBookings);
            case "WAITING" -> when(bookingRepository.findByStatusAndBookerIdOrderByStartDesc(Booking.BookingStatus.WAITING, userId)).thenReturn(mockBookings);
            case "REJECTED" -> when(bookingRepository.findByStatusAndBookerIdOrderByStartDesc(Booking.BookingStatus.REJECTED, userId)).thenReturn(mockBookings);
            default -> when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(mockBookings);
        }

        List<BookingDto> result = bookingService.getBookings(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @ParameterizedTest
    @DisplayName("Получение списка бронирований пользователя по состоянию")
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "UNKNOWN"})
    void getBookings_byState_shouldReturnList(String state) {
        long userId = user.getId();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.BookingStatus.WAITING)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);

        switch (state.toUpperCase()) {
            case "CURRENT" ->
                    when(bookingRepository.findCurrentBookings(eq(userId), any())).thenReturn(List.of(booking));
            case "PAST" ->
                    when(bookingRepository.findPastBookings(eq(userId), any())).thenReturn(List.of(booking));
            case "FUTURE" ->
                    when(bookingRepository.findFutureBookings(eq(userId), any())).thenReturn(List.of(booking));
            case "WAITING" ->
                    when(bookingRepository.findByStatusAndBookerIdOrderByStartDesc(Booking.BookingStatus.WAITING, userId)).thenReturn(List.of(booking));
            case "REJECTED" ->
                    when(bookingRepository.findByStatusAndBookerIdOrderByStartDesc(Booking.BookingStatus.REJECTED, userId)).thenReturn(List.of(booking));
            default ->
                    when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(List.of(booking));
        }

        List<BookingDto> result = bookingService.getBookings(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @ParameterizedTest
    @DisplayName("Получение списка бронирований для владельца по состоянию")
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "UNKNOWN"})
    void getBookingsForOwner_byState_shouldReturnList(String state) {
        long ownerId = owner.getId();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.BookingStatus.WAITING)
                .build();

        when(userRepository.existsById(ownerId)).thenReturn(true);

        switch (state.toUpperCase()) {
            case "CURRENT" ->
                    when(bookingRepository.findCurrentBookingsByOwner(eq(ownerId), any())).thenReturn(List.of(booking));
            case "PAST" ->
                    when(bookingRepository.findPastBookingsByOwner(eq(ownerId), any())).thenReturn(List.of(booking));
            case "FUTURE" ->
                    when(bookingRepository.findFutureBookingsByOwner(eq(ownerId), any())).thenReturn(List.of(booking));
            case "WAITING" ->
                    when(bookingRepository.findByStatusAndItemOwnerIdOrderByStartDesc(Booking.BookingStatus.WAITING, ownerId)).thenReturn(List.of(booking));
            case "REJECTED" ->
                    when(bookingRepository.findByStatusAndItemOwnerIdOrderByStartDesc(Booking.BookingStatus.REJECTED, ownerId)).thenReturn(List.of(booking));
            default ->
                    when(bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId)).thenReturn(List.of(booking));
        }

        List<BookingDto> result = bookingService.getBookingsForOwner(ownerId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Подтверждение бронирования - успех")
    void approveBooking_success() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Booking.BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingDto result = bookingService.approveBooking(1L, owner.getId(), true);
        assertEquals(Booking.BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    @DisplayName("Подтверждение бронирования - уже подтверждено")
    void approveBooking_alreadyProcessed() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Booking.BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, owner.getId(), true));
    }

    @Test
    @DisplayName("Подтверждение бронирования - не владелец вещи")
    void approveBooking_notOwner() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Booking.BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        long notOwnerId = 99L;

        assertThrows(UnauthorizedException.class,
                () -> bookingService.approveBooking(1L, notOwnerId, true));
    }

    @Test
    @DisplayName("Отмена бронирования - успешно")
    void cancelBooking_success() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.BookingStatus.WAITING)
                .created(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingDto result = bookingService.cancelBooking(1L, user.getId());
        assertEquals(Booking.BookingStatus.CANCELED, result.getStatus());
    }

    @Test
    @DisplayName("Отмена бронирования - уже началось")
    void cancelBooking_tooLate() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(Booking.BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.cancelBooking(1L, user.getId()));
    }
}