package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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