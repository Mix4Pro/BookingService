package booking.repository;

import booking.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomRepository extends JpaRepository<RoomEntity, Long>,
    JpaSpecificationExecutor<RoomEntity> {
}
