package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utility.HttpHeaders;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(@RequestHeader(HttpHeaders.USER_ID) Long ownerId,
                                  @RequestBody ItemCreateDto dto) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader(HttpHeaders.USER_ID) Long ownerId,
                                  @PathVariable Long itemId,
                                  @RequestBody ItemCreateDto newDto) {
        return itemService.update(ownerId, itemId, newDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable(name = "itemId") Long id,
                       @RequestHeader(HttpHeaders.USER_ID) Long ownerId) {
        itemService.delete(id, ownerId);
    }

    @GetMapping
    public List<ItemResponseDto> findAllByOwner(@RequestHeader(HttpHeaders.USER_ID) Long ownerId) {
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findById(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                    @PathVariable(name = "itemId") Long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto dto) {
        return itemService.addComment(userId, itemId, dto);
    }
}
