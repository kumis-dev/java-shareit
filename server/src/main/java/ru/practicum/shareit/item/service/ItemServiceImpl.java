package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponseDto create(Long ownerId, ItemCreateDto dto) {
        // проверяем существование пользователя
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.mapFromCreateDto(dto);
        item.setOwner(owner);
        if (dto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(dto.getRequestId()).orElseThrow(
                    () -> new NotFoundException("Запрос не найден"));
            item.setItemRequest(itemRequest);
        }
        return ItemMapper.mapToItemDto(itemRepository.save(item),
                null,
                null,
                List.of());
    }

    @Override
    public ItemResponseDto update(Long ownerId, Long itemId, ItemCreateDto newDto) {
        Item item = getItem(itemId);

        if (!isOwner(item, ownerId))  // проверяем существующего владельца
            throw new ForbiddenException("Редактировать может только владелец");

        Item newItem = ItemMapper.mapFromCreateDto(newDto);
        // обновляем поля item новыми полями из newItem
        ItemMapper.updateFields(item, newItem);
        return ItemMapper.mapToItemDto(itemRepository.save(item),
                null,
                null,
                List.of());
    }

    @Override
    public void delete(Long id, Long ownerId) {
        if (!isOwner(getItem(id), ownerId))  // проверяем существующего владельца
            throw new ForbiddenException("Удалять может только владелец");
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemResponseDto> findAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwner_Id(ownerId)
                .stream()
                .map(item -> ItemMapper.mapToItemDto(
                        item,
                        getLastBooking(ownerId, item.getId()),
                        getNextBooking(ownerId, item.getId()),
                        getComments(item.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto findById(Long userId, Long itemId) {
        Item item = getItem(itemId);

        BookingShortDto last = null;
        BookingShortDto next = null;

        if (isOwner(item, userId)) {
            last = getLastBooking(userId, itemId);
            next = getNextBooking(userId, itemId);
        }

        return ItemMapper.mapToItemDto(
                item,
                last,
                next,
                getComments(itemId)
        );
    }

    @Override
    public List<ItemResponseDto> search(String text) {
        if (text == null || text.isBlank())
            return List.of();

        return itemRepository.search(text)
                .stream()
                .filter(Item::getAvailable)
                .map(item -> ItemMapper.mapToItemDto(
                        item,
                        null,
                        null,
                        List.of()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = getItem(itemId);
        boolean hasBooking =
                bookingRepository.findByBooker_IdAndEndTimeIsBefore(userId, LocalDateTime.now())
                        .stream()
                        .anyMatch(booking -> booking.getItem().getId().equals(itemId));
        if (!hasBooking)
            throw new BadRequestException("Комментарий можно оставить только после аренды");
        return CommentMapper.mapToDto(
                commentRepository.save(
                        CommentMapper.mapToComment(dto, user, item)
                )
        );
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private boolean isOwner(Item item, Long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToDto)
                .toList();
    }

    private BookingShortDto getLastBooking(Long ownerId, Long itemId) {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndTimeIsBefore(ownerId, LocalDateTime.now());
        return bookings.isEmpty() ? null : BookingMapper.mapToShortDto(bookings.get(0));
    }

    private BookingShortDto getNextBooking(Long ownerId, Long itemId) {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartTimeIsAfter(ownerId, LocalDateTime.now());
        return bookings.isEmpty() ? null : BookingMapper.mapToShortDto(bookings.get(bookings.size() - 1));
    }
}
