package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemShortDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserDto(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingCreateDto dto, User booker, Item item) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(Booking.BookingStatus.WAITING)
                .created(LocalDateTime.now())
                .build();
    }
}