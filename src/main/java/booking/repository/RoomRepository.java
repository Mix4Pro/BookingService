package booking.repository;

import booking.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomEntity,Long> {
    boolean existsByNumber(String number);
}
