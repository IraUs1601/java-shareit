package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для BookingMapper")
public class BookingMapperTest {

    private final User booker = new User(1L, "John Doe", "john@example.com");
    private final Item item = new Item(2L, "Item Name", "Item Description", true, booker, null);
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    @Test
    @DisplayName("Преобразование Booking в BookingDto")
    void toBookingDto_ShouldMapCorrectly() {
        Booking booking = Booking.builder()
                .id(10L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Booking.BookingStatus.APPROVED)
                .created(LocalDateTime.now())
                .build();

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());

        ItemShortDto itemDto = dto.getItem();
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());

        UserDto userDto = dto.getBooker();
        assertNotNull(userDto);
        assertEquals(booker.getId(), userDto.getId());
        assertEquals(booker.getName(), userDto.getName());
        assertEquals(booker.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("Преобразование BookingCreateDto в Booking")
    void toBooking_ShouldMapCorrectly() {
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        Booking booking = BookingMapper.toBooking(createDto, booker, item);

        assertNotNull(booking);
        assertEquals(createDto.getStart(), booking.getStart());
        assertEquals(createDto.getEnd(), booking.getEnd());
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(Booking.BookingStatus.WAITING, booking.getStatus());
        assertNotNull(booking.getCreated());
    }
}