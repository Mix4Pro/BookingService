package booking.repository;

import booking.entity.BookingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingHistoryRepository extends JpaRepository<BookingHistoryEntity, Long> {
}
