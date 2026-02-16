package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    // мои запросы
    List<ItemRequest> findByRequester_Id(Long requesterId);
}
