package booking.service;

import booking.component.kafka.producer.BookingKafkaProducer;
import booking.constant.enums.*;
import booking.dto.request.PaymentRequestDto;
import booking.dto.response.PaymentHistoryResponseDto;
import booking.entity.*;
import booking.exception.booking.BookingNotFoundException;
import booking.exception.booking.BookingStatusNotConfirmedException;
import booking.mapper.PaymentMapper;
import booking.repository.BookingRepository;
import booking.repository.PaymentHistoryRepository;
import booking.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private PaymentHistoryRepository paymentHistoryRepository;
    @Mock private BookingKafkaProducer bookingKafkaProducer;
    @Mock private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private BookingEntity booking;
    private PaymentRequestDto request;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFirstName("Michael");
        user.setLastName("Jordan");
        user.setEmail("jordan23@gmail.com");
        user.setCardToken("79c3d4a3-6467-43dd-89f4-6c38f4f1ee5c");

        CancellationPolicyEntity policy = new CancellationPolicyEntity();
        policy.setHoursBeforeCheckIn(24);
        policy.setRefundPercentage(new BigDecimal("100.00"));

        booking = new BookingEntity();
        booking.setId(1L);
        booking.setUser(user);
        booking.setTotalAmount(new BigDecimal("500.00"));
        booking.setPrepaymentAmount(new BigDecimal("150.00"));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentPlan(PaymentPlan.PREPAYMENT);
        booking.setCancellationPolicy(policy);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(10));
        booking.setCreatedAt(LocalDateTime.now());

        request = new PaymentRequestDto(
            UUID.randomUUID(),
            TransactionType.PAYMENT,
            CurrencyType.UZS,
            UUID.randomUUID(),
            "Michael Jordan",
            "79c3d4a3-6467-43dd-89f4-6c38f4f1ee5c",
            "HoYo Booking Service",
            "4c486a4c-cb98-424a-8cd3-c34a9fd76985"
        );
    }


    @Test
    void charge_shouldSavePendingPayment_whenBookingIsConfirmed() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentMapper.toResponse(any())).thenReturn(mock(PaymentHistoryResponseDto.class));

        paymentService.charge(1L, request);

        verify(paymentHistoryRepository).save(argThat(p ->
            p.getStatus() == PaymentHistoryPaymentStatus.PENDING &&
                p.getType() == PaymentHistoryPaymentType.CHARGE
        ));
        verify(bookingKafkaProducer).sendBookingPayment(any(), any(), any());
    }

    @Test
    void charge_shouldChargePrepaymentAmount_whenPlanIsPrepayment() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentMapper.toResponse(any())).thenReturn(mock(PaymentHistoryResponseDto.class));

        paymentService.charge(1L, request);

        verify(paymentHistoryRepository).save(argThat(p ->
            p.getAmount().compareTo(new BigDecimal("150.00")) == 0
        ));
    }

    @Test
    void charge_shouldChargeFullAmount_whenPlanIsFull() {
        booking.setPaymentPlan(PaymentPlan.FULL);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentMapper.toResponse(any())).thenReturn(mock(PaymentHistoryResponseDto.class));

        paymentService.charge(1L, request);

        verify(paymentHistoryRepository).save(argThat(p ->
            p.getAmount().compareTo(new BigDecimal("500.00")) == 0
        ));
    }

    @Test
    void charge_shouldThrow_whenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.charge(99L, request))
            .isInstanceOf(BookingNotFoundException.class);

        verifyNoInteractions(paymentHistoryRepository, bookingKafkaProducer);
    }

    @Test
    void charge_shouldThrow_whenBookingIsHold() {
        booking.setStatus(BookingStatus.HOLD);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.charge(1L, request))
            .isInstanceOf(BookingStatusNotConfirmedException.class);

        verifyNoInteractions(paymentHistoryRepository, bookingKafkaProducer);
    }

    @Test
    void charge_shouldThrow_whenPaymentPlanIsNoPrePay() {
        booking.setPaymentPlan(PaymentPlan.NO_PREPAY);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.charge(1L, request))
            .isInstanceOf(BookingStatusNotConfirmedException.class)
            .hasMessageContaining("NO_PREPAY");

        verifyNoInteractions(paymentHistoryRepository, bookingKafkaProducer);
    }


    @Test
    void refund_shouldSavePendingRefund_whenBookingIsPaid() {
        booking.setStatus(BookingStatus.PAID);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentMapper.toResponse(any())).thenReturn(mock(PaymentHistoryResponseDto.class));

        paymentService.refund(1L);

        verify(paymentHistoryRepository).save(argThat(p ->
            p.getStatus() == PaymentHistoryPaymentStatus.PENDING &&
                p.getType() == PaymentHistoryPaymentType.REFUND
        ));
        verify(bookingKafkaProducer).sendBookingRefund(any(), any());
    }

    @Test
    void refund_shouldCalculateCorrectAmount_whenPolicyIs100Percent() {
        booking.setStatus(BookingStatus.PAID);
        // policy = 100% возврат, checkIn через 5 дней (> 24 часов)
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentMapper.toResponse(any())).thenReturn(mock(PaymentHistoryResponseDto.class));

        paymentService.refund(1L);

        // prepaymentAmount=150, policy=100% → refund=150
        verify(paymentHistoryRepository).save(argThat(p ->
            p.getAmount().compareTo(new BigDecimal("150.00")) == 0
        ));
    }

    @Test
    void refund_shouldThrow_whenBookingIsNotPaid() {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.refund(1L))
            .isInstanceOf(BookingStatusNotConfirmedException.class)
            .hasMessageContaining("PAID");

        verifyNoInteractions(paymentHistoryRepository, bookingKafkaProducer);
    }

    @Test
    void refund_shouldThrow_whenPlanIsNoPrePay() {
        booking.setStatus(BookingStatus.PAID);
        booking.setPaymentPlan(PaymentPlan.NO_PREPAY);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.refund(1L))
            .isInstanceOf(BookingStatusNotConfirmedException.class)
            .hasMessageContaining("NO_PREPAY");

        verifyNoInteractions(paymentHistoryRepository, bookingKafkaProducer);
    }

    @Test
    void refund_shouldThrow_whenDeadlinePassed() {
        booking.setStatus(BookingStatus.PAID);
        booking.setCheckInDate(LocalDate.now().minusDays(1)); // дедлайн прошёл
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.refund(1L))
            .isInstanceOf(BookingStatusNotConfirmedException.class)
            .hasMessageContaining("0");

        verifyNoInteractions(bookingKafkaProducer);
    }

    @Test
    void refund_shouldThrow_whenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.refund(99L))
            .isInstanceOf(BookingNotFoundException.class);

        verifyNoInteractions(paymentHistoryRepository, bookingKafkaProducer);
    }
}
