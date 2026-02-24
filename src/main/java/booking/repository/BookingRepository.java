//package booking.repository;
//
//import booking.constant.enums.BookingStatus;
//import booking.entity.BookingEntity;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public interface BookingRepository extends JpaRepository<BookingEntity,Long> {
//    @Query("""
//        SELECT b FROM BookingEntity b
//        WHERE b.room.id = :roomId
//            AND b.status != 'CANCELLED'
//            AND b.checkIn < :checkOut
//            AND b.checkOut > :checkIn
//        """)
//    List<BookingEntity> findConflictingBookings(
//        @Param("roomId")
//        Long roomId,
//
//        @Param("checkOut")
//        LocalDate checkOut,
//
//        @Param("checkIn")
//        LocalDate checkIn
//    );
//    Page<BookingEntity> findByStatus(BookingStatus status, Pageable pageable);
//}
