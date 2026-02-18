package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utility.HttpHeaders;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Создание вещи {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи {}, itemId={}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(HttpHeaders.USER_ID) Long userId) {
        log.info("Получение вещи itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(HttpHeaders.USER_ID) Long userId) {
        log.info("Получение вещей пользователя userId={}", userId);
        return itemClient.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader(HttpHeaders.USER_ID) Long userId) {
        log.info("Поиск вещей по тексту='{}', userId={}", text, userId);
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Добавление комментария {}, itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}