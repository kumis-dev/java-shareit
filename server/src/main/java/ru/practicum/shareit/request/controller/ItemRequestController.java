package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utility.HttpHeaders;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                 @RequestBody ItemRequestCreateDto dto) {
        return itemRequestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUser(@RequestHeader(HttpHeaders.USER_ID) Long userId) {
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(HttpHeaders.USER_ID) Long userId) {
        return itemRequestService.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                   @PathVariable Long requestId) {
        return itemRequestService.findById(userId, requestId);
    }

    @DeleteMapping("/{requestId}")
    public void delete(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                       @PathVariable Long requestId) {
        itemRequestService.delete(userId, requestId);
    }
}
