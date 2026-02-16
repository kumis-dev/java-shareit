package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // для бронирующего
    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByBooker_IdAndEndTimeIsBefore(
            Long bookerId, LocalDateTime time
    );

    List<Booking> findByBooker_IdAndStartTimeIsAfter(
            Long bookerId, LocalDateTime time
    );

    List<Booking> findByBooker_IdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end
    );

    // для владельца вещей
    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByItem_Owner_IdAndEndTimeIsBefore(
            Long ownerId, LocalDateTime time
    );

    List<Booking> findByItem_Owner_IdAndStartTimeIsAfter(
            Long ownerId, LocalDateTime time
    );

    // _Id - значит берем id у связанной сущности Owner, а Owner возьмет у Item
    List<Booking> findByItem_Owner_IdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end
    );
}

