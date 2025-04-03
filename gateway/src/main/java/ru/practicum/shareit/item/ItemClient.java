package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemCreateDto dto) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, ItemDto dto) {
        return patch("/" + itemId, ownerId, dto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(Long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}