package booking.service;

import booking.component.kafka.producer.BookingKafkaProducer;
import booking.constant.enums.*;
import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.entity.*;
import booking.exception.RateNotFoundException;
import booking.exception.UserNotFoundException;
import booking.exception.booking.*;
import booking.exception.room.RoomNotFoundException;
import booking.mapper.BookingMapper;
import booking.repository.*;
import booking.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private UserRepository userRepository;
    @Mock private RateRepository rateRepository;
    @Mock private CancellationPolicyRepository cancellationPolicyRepository;
    @Mock private BookingHistoryRepository bookingHistoryRepository;
    @Mock private RoomAvailabilityRepository roomAvailabilityRepository;
    @Mock private BookingKafkaProducer bookingKafkaProducer;
    @Mock private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private RoomEntity room;
    private UserEntity user;
    private RateEntity rate;
    private CancellationPolicyEntity policy;
    private BookingRequestDto request;
    private BookingEntity booking;

    @BeforeEach
    void setUp() {
        room = new RoomEntity();
        room.setId(1L);

        user = new UserEntity();
        user.setId(1L);
        user.setFirstName("Michael");
        user.setLastName("Jordan");
        user.setEmail("jordan23@gmail.com");
        user.setCardToken("79c3d4a3-6467-43dd-89f4-6c38f4f1ee5c");

        rate = new RateEntity();
        rate.setId(1L);
        rate.setPricePerNight(new BigDecimal("100.00"));

        policy = new CancellationPolicyEntity();
        policy.setId(1L);
        policy.setHoursBeforeCheckIn(24);
        policy.setRefundPercentage(new BigDecimal("100.00"));

        request = new BookingRequestDto(
            1L,
            1L,
            1L,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(5),
            2,
            1L,
            PaymentPlan.PREPAYMENT
        );

        booking = new BookingEntity();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setRate(rate);
        booking.setCancellationPolicy(policy);
        booking.setStatus(BookingStatus.HOLD);
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(5));
        booking.setTotalAmount(new BigDecimal("400.00"));
        booking.setPrepaymentAmount(new BigDecimal("120.00"));
        booking.setPaymentPlan(PaymentPlan.PREPAYMENT);
        booking.setCreatedAt(LocalDateTime.now());
    }


    @Test
    void holdARoom_shouldCreateHoldBooking_whenAllDataIsValid() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(cancellationPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(bookingRepository.findConflictingBookings(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseFromEntity(any())).thenReturn(mock(BookingResponseDto.class));

        bookingService.holdARoom(request);

        verify(bookingRepository).save(argThat(b ->
            b.getStatus() == BookingStatus.HOLD &&
                b.getPaymentPlan() == PaymentPlan.PREPAYMENT
        ));
        verify(bookingHistoryRepository).save(any());
    }

    @Test
    void holdARoom_shouldThrow_whenCheckOutBeforeCheckIn() {
        BookingRequestDto invalidRequest = new BookingRequestDto(
            1L, 1L, 1L,
            LocalDate.now().plusDays(5),  // checkIn позже
            LocalDate.now().plusDays(1),  // checkOut раньше
            2, 1L, PaymentPlan.PREPAYMENT
        );

        assertThatThrownBy(() -> bookingService.holdARoom(invalidRequest))
            .isInstanceOf(BookingCheckoutBeforeCheckInException.class);

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void holdARoom_shouldThrow_whenCheckInEqualsCheckOut() {
        LocalDate sameDate = LocalDate.now().plusDays(3);
        BookingRequestDto invalidRequest = new BookingRequestDto(
            1L, 1L, 1L,
            sameDate,
            sameDate,
            2, 1L, PaymentPlan.PREPAYMENT
        );

        assertThatThrownBy(() -> bookingService.holdARoom(invalidRequest))
            .isInstanceOf(BookingCheckoutBeforeCheckInException.class);

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void holdARoom_shouldThrow_whenRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.holdARoom(request))
            .isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    void holdARoom_shouldThrow_whenUserNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.holdARoom(request))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void holdARoom_shouldThrow_whenRateNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.holdARoom(request))
            .isInstanceOf(RateNotFoundException.class);
    }

    @Test
    void holdARoom_shouldThrow_whenRoomAlreadyBooked() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(cancellationPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(bookingRepository.findConflictingBookings(any(), any(), any(), any()))
            .thenReturn(List.of(booking));  // конфликт есть

        assertThatThrownBy(() -> bookingService.holdARoom(request))
            .isInstanceOf(RoomStatusNotHandleException.class)
            .hasMessageContaining("already booked");
    }

    @Test
    void holdARoom_shouldCalculateCorrectAmounts() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(cancellationPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(bookingRepository.findConflictingBookings(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseFromEntity(any())).thenReturn(mock(BookingResponseDto.class));

        bookingService.holdARoom(request);

        // 4 ночи * 100 = 400 total, 400 * 30% = 120 prepayment
        verify(bookingRepository).save(argThat(b ->
            b.getTotalAmount().compareTo(new BigDecimal("400.00")) == 0 &&
                b.getPrepaymentAmount().compareTo(new BigDecimal("120.00")) == 0
        ));
    }


    @Test
    void confirmARoom_shouldChangeStatus_whenBookingIsHold() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponseFromEntity(any())).thenReturn(mock(BookingResponseDto.class));

        bookingService.confirmARoom(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(bookingHistoryRepository).save(any());
        verify(bookingKafkaProducer).sendBookingNotification(any());
    }

    @Test
    void confirmARoom_shouldThrow_whenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.confirmARoom(99L))
            .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void confirmARoom_shouldThrow_whenBookingIsNotHold() {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.confirmARoom(1L))
            .isInstanceOf(RoomStatusNotHandleException.class);
    }


    @Test
    void cancel_shouldChangeStatus_whenBookingIsHold() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseFromEntity(any())).thenReturn(mock(BookingResponseDto.class));

        bookingService.cancel(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingHistoryRepository).save(any());
        verify(bookingKafkaProducer).sendBookingNotification(any());
    }

    @Test
    void cancel_shouldChangeStatus_whenBookingIsConfirmed() {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseFromEntity(any())).thenReturn(mock(BookingResponseDto.class));

        bookingService.cancel(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancel_shouldThrow_whenBookingIsPaid() {
        booking.setStatus(BookingStatus.PAID);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancel(1L))
            .isInstanceOf(BookingStatusNotConfirmedException.class)
            .hasMessageContaining("HOLD or CONFIRMED");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancel_shouldThrow_whenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancel(99L))
            .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void cancel_shouldSaveHistoryWithCorrectStatuses() {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseFromEntity(any())).thenReturn(mock(BookingResponseDto.class));

        bookingService.cancel(1L);

        verify(bookingHistoryRepository).save(argThat(h ->
            h.getStatusFrom() == BookingStatus.CONFIRMED &&
                h.getStatusTo() == BookingStatus.CANCELLED &&
                h.getChangedBy() == BookingChangeSource.USER
        ));
    }
}