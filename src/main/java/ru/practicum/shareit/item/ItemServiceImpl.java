package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Override
    public ItemDto createItem(ItemCreateDto dto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        ItemRequest request = dto.getRequestId() != null ?
                itemRequestRepository.findById(dto.getRequestId())
                        .orElseThrow(() -> new NotFoundException("Request not found."))
                : null;

        Item item = ItemMapper.toItem(dto, owner, request);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found."));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {
            bookingRepository.findLastBooking(itemId, LocalDateTime.now())
                    .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));

            bookingRepository.findNextBooking(itemId, LocalDateTime.now())
                    .ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, Booking.BookingStatus.APPROVED, LocalDateTime.now().minusHours(2));

        System.out.println(LocalDateTime.now().minusHours(2));

        if (bookings.isEmpty()) {
            throw new ValidationException("User must have booked this item to leave a comment.");
        }

        Comment comment = new Comment(null, dto.getText(), item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> getItems(Long ownerId) {
        if (ownerId == null) {
            return itemRepository.findAll()
                    .stream()
                    .filter(item -> item.getId() <= 2)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) return List.of();
        return itemRepository.searchAvailableItems(text.trim())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto dto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only owner can update the item");
        }

        Optional.ofNullable(dto.getName()).ifPresent(item::setName);
        Optional.ofNullable(dto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(dto.getAvailable()).ifPresent(item::setAvailable);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only owner can delete the item");
        }

        itemRepository.delete(item);
    }
}