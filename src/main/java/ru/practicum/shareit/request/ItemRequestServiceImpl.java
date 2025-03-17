package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestCreateDto dto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        ItemRequest request = itemRequestRepository.save(ItemRequestMapper.toItemRequest(dto, requestor));

        List<Item> items = List.of();

        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    @Override
    public List<ItemDto> getItemsByRequest(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found."));

        return itemRepository.findByRequest(request).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long requestorId) {
        return itemRequestRepository.findByRequestorId(requestorId).stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequest(request);
                    return ItemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        return itemRequestRepository.findAll().stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequest(request);
                    return ItemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found."));

        List<ItemDto> items = itemRepository.findByRequest(request).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), items);
    }
}