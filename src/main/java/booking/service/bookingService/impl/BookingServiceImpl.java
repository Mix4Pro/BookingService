package booking.service.bookingService.impl;

import booking.component.adapter.kafka.consumer.BookingKafkaConsumer;
import booking.component.adapter.kafka.producer.BookingKafkaProducer;
import booking.constant.enums.BookingStatus;
import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingPageResponseDto;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.entity.BookingEntity;
import booking.entity.RoomEntity;
import booking.exception.bookingException.BookingCheckoutBeforeCheckInException;
import booking.exception.bookingException.BookingStatusAlterationException;
import booking.exception.bookingException.BookingNotFoundException;
import booking.exception.bookingException.RoomAlreadyBookedException;
import booking.exception.roomException.RoomNotFoundException;
import booking.mapper.BookingMapper;
import booking.repository.BookingRepository;
import booking.repository.RoomRepository;
import booking.service.bookingService.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingMapper bookingMapper;

    private final BookingKafkaProducer bookingKafkaProducer;

    @Transactional
    public BookingResponseDto createBooking (BookingRequestDto bookingRequestDto) {
        if(!bookingRequestDto.checkOut().isAfter(bookingRequestDto.checkIn())) {
            throw new BookingCheckoutBeforeCheckInException("Check out should be after check in",HttpStatus.BAD_REQUEST);
        }
        List<BookingEntity> conflictingBookings = bookingRepository.findConflictingBookings(
            bookingRequestDto.roomId(),
            bookingRequestDto.checkOut(),
            bookingRequestDto.checkIn()
        );

        if(!conflictingBookings.isEmpty()){
            throw new RoomAlreadyBookedException("Room is already booked", HttpStatus.CONFLICT);
        }

        RoomEntity room = roomRepository.findById(bookingRequestDto.roomId())
            .orElseThrow(()-> new RoomNotFoundException("Room not found",HttpStatus.NOT_FOUND));

        BookingEntity booking = bookingMapper.toEntityFromRequest(bookingRequestDto);
        booking.setRoom(room);
        BookingEntity bookingSaved = bookingRepository.save(booking);
        log.info("Data is saved in the DB");

        bookingKafkaProducer.sendBookingCreatedEvent(bookingSaved);
        log.info("Data is sent to kafka broker id = {}",bookingSaved.getId());

        return bookingMapper.toResponseFromEntity(bookingSaved);
    }

    @Transactional
    public BookingResponseDto confirmBooking (long id) {
        BookingEntity booking = bookingRepository.findById(id)
            .orElseThrow(()-> new BookingNotFoundException("Booking not found",HttpStatus.NOT_FOUND));

        log.error("{}",booking.getStatus());

        if(!booking.getStatus().equals(BookingStatus.HOLD)) {
            throw new BookingStatusAlterationException("Booking status is not pending", HttpStatus.CONFLICT);
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingMapper.toResponseFromEntity(booking);
    }

    @Transactional
    public BookingResponseDto cancelBooking (long id) {
        BookingEntity booking = bookingRepository.findById(id)
            .orElseThrow(()-> new BookingNotFoundException("Booking not found",HttpStatus.NOT_FOUND));

        if(!booking.getStatus().equals(BookingStatus.HOLD) &&
            !booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new BookingStatusAlterationException("Booking status is neither PENDING nor CONFIRMED",HttpStatus.CONFLICT);
        }
        booking.setStatus(BookingStatus.CANCELLED);

        return bookingMapper.toResponseFromEntity(booking);
    }

    public BookingPageResponseDto findBookingsByStatus (BookingStatus status, Pageable pageable) {

        Page<BookingEntity> bookings = bookingRepository.findByStatus(status,pageable);

        List<BookingResponseDto> bookingsList = bookings
            .getContent()
            .stream()
            .map(bookingMapper::toResponseFromEntity)
            .toList();

        return new BookingPageResponseDto(
            bookingsList,
            bookings.getNumber(),
            bookings.getSize(),
            bookings.getTotalElements(),
            bookings.getTotalPages()
        );
    }
}
