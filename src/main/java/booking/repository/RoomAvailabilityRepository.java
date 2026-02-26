package booking.repository;

import booking.entity.RoomAvailabilityEntity;
import booking.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailabilityEntity,Long> {
    Optional<RoomAvailabilityEntity> findByRoomAndDate(RoomEntity room, LocalDate date);

}
