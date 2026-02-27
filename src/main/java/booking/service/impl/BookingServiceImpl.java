package booking.service.impl;

import booking.component.kafka.producer.BookingKafkaProducer;
import booking.constant.enums.BookingChangeSource;
import booking.constant.enums.BookingStatus;
import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.entity.*;
import booking.exception.CancellationPolicyNotFoundException;
import booking.exception.RateNotFoundException;
import booking.exception.UserNotFoundException;
import booking.exception.booking.BookingCheckoutBeforeCheckInException;
import booking.exception.booking.BookingNotFoundException;
import booking.exception.booking.BookingStatusNotConfirmedException;
import booking.exception.booking.RoomStatusNotHandleException;
import booking.exception.room.RoomNotFoundException;
import booking.mapper.BookingMapper;
import booking.repository.*;
import booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RateRepository rateRepository;
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final BookingHistoryRepository bookingHistoryRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    private final BookingKafkaProducer bookingKafkaProducer;

    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponseDto holdARoom(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.checkOutDate().isBefore(bookingRequestDto.checkInDate()) ||
                bookingRequestDto.checkOutDate().isEqual(bookingRequestDto.checkInDate())) {
            throw new BookingCheckoutBeforeCheckInException("Check out can't be set before or on the same day as check in",
                    HttpStatus.CONFLICT);
        }

        // Creating hold
        log.info("roomId {}", bookingRequestDto.roomId());
        RoomEntity room = roomRepository.findById(bookingRequestDto.roomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found", HttpStatus.NOT_FOUND));

        log.info("roomId {}", bookingRequestDto.userId());
        UserEntity user = userRepository.findById(bookingRequestDto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));


        log.info("roomId {}", bookingRequestDto.rateId());
        RateEntity rate = rateRepository.findById(bookingRequestDto.rateId())
                .orElseThrow(() -> new RateNotFoundException("Rate not found", HttpStatus.NOT_FOUND));

        CancellationPolicyEntity cancellationPolicy = cancellationPolicyRepository
                .findById(bookingRequestDto.cancellationPolicyId())
                .orElseThrow(() -> new CancellationPolicyNotFoundException(
                        "Cancellation policy not found",
                        HttpStatus.NOT_FOUND));


        List<BookingEntity> conflicts = bookingRepository.findConflictingBookings(
                bookingRequestDto.roomId(),
                bookingRequestDto.checkOutDate(),
                bookingRequestDto.checkInDate(),
                LocalDateTime.now()
        );

        if (!conflicts.isEmpty()) {
            throw new RoomStatusNotHandleException("Room already booked", HttpStatus.CONFLICT);
        }

        long nights = ChronoUnit.DAYS.between(bookingRequestDto.checkInDate(),
                bookingRequestDto.checkOutDate());

        BigDecimal totalAmount = rate.getPricePerNight().multiply(new BigDecimal(nights));
        BigDecimal prepaymentAmount = totalAmount.multiply(new BigDecimal("0.30")); // 30% предоплата

        BookingEntity booking = BookingEntity
                .builder()
                .user(user)
                .room(room)
                .rate(rate)
                .cancellationPolicy(cancellationPolicy)
                .checkInDate(bookingRequestDto.checkInDate())
                .checkOutDate(bookingRequestDto.checkOutDate())
                .guestsCount(bookingRequestDto.guestsCount())
                .totalAmount(totalAmount)
                .prepaymentAmount(prepaymentAmount)
                .status(BookingStatus.HOLD)
                .paymentPlan(bookingRequestDto.paymentPlan())
                .createdAt(LocalDateTime.now())
                .holdExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        booking = bookingRepository.save(booking);

        // Saving in the booking history
        BookingHistoryEntity bookingHistory = BookingHistoryEntity
                .builder()
                .booking(booking)
                .statusFrom(null)
                .statusTo(BookingStatus.HOLD)
                .changedAt(LocalDateTime.now())
                .changedBy(BookingChangeSource.USER)
                .comment("Booking created")
                .build();

        bookingHistoryRepository.save(bookingHistory);

        blockRoomDates(room, bookingRequestDto.checkInDate(), bookingRequestDto.checkOutDate());

        return bookingMapper.toResponseFromEntity(booking);
    }

    @Transactional
    public void blockRoomDates(RoomEntity room, LocalDate checkIn, LocalDate checkOut) {
        LocalDate date = checkIn;
        while (!date.isEqual(checkOut)) { // checkOut обычно не включаем
            LocalDate finalDate = date;
            RoomAvailabilityEntity availability = roomAvailabilityRepository
                    .findByRoomAndDate(room, date)
                    .orElseGet(() -> {
                        RoomAvailabilityEntity newAvail = new RoomAvailabilityEntity();
                        newAvail.setRoom(room);
                        newAvail.setDate(finalDate);
                        return newAvail;
                    });

            availability.setIsAvailable(false); // блокируем дату
            roomAvailabilityRepository.save(availability); // используем объект, а не класс

            date = date.plusDays(1);
        }
    }

    @Transactional
    public BookingResponseDto confirmARoom(Long id) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found", HttpStatus.NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.HOLD)) {
            throw new RoomStatusNotHandleException("Room status is not HADLE", HttpStatus.CONFLICT);
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        BookingHistoryEntity bookingHistory = BookingHistoryEntity
                .builder()
                .booking(booking)
                .statusFrom(BookingStatus.HOLD)
                .statusTo(BookingStatus.CONFIRMED)
                .changedAt(LocalDateTime.now())
                .changedBy(BookingChangeSource.SYSTEM)
                .comment("Booking confirmed")
                .build();


        bookingHistoryRepository.save(bookingHistory);

        bookingKafkaProducer.sendBookingNotification(booking);

        return bookingMapper.toResponseFromEntity(booking);
    }

    public BookingResponseDto cancel(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found", HttpStatus.NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.HOLD) &&
                !booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new BookingStatusNotConfirmedException(
                    "Only HOLD or CONFIRMED bookings can be cancelled", HttpStatus.CONFLICT);
        }

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        BookingHistoryEntity history = BookingHistoryEntity.builder()
                .booking(booking)
                .statusFrom(previousStatus)
                .statusTo(BookingStatus.CANCELLED)
                .changedAt(LocalDateTime.now())
                .changedBy(BookingChangeSource.USER)
                .comment("Booking cancelled by user")
                .build();
        bookingHistoryRepository.save(history);

        bookingKafkaProducer.sendBookingNotification(booking);

        log.info("Booking cancelled: bookingId={}", bookingId);
        return bookingMapper.toResponseFromEntity(booking);
    }

}
