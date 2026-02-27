package booking.service.impl;

import booking.component.kafka.producer.BookingKafkaProducer;
import booking.constant.enums.BookingChangeSource;
import booking.constant.enums.BookingStatus;
import booking.constant.enums.PaymentHistoryPaymentStatus;
import booking.constant.enums.PaymentHistoryPaymentType;
import booking.constant.enums.PaymentPlan;
import booking.dto.request.PaymentRequestDto;
import booking.dto.response.PaymentHistoryResponseDto;
import booking.entity.BookingEntity;
import booking.entity.BookingHistoryEntity;
import booking.entity.PaymentHistoryEntity;
import booking.exception.booking.BookingNotFoundException;
import booking.exception.booking.BookingStatusNotConfirmedException;
import booking.mapper.PaymentMapper;
import booking.repository.BookingHistoryRepository;
import booking.repository.BookingRepository;
import booking.repository.PaymentHistoryRepository;
import booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final BookingKafkaProducer bookingKafkaProducer;
    private final PaymentMapper paymentMapper;
    private final BookingHistoryRepository bookingHistoryRepository;

    public PaymentHistoryResponseDto charge(Long bookingId, PaymentRequestDto paymentRequestDto) {
        BookingEntity booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found", HttpStatus.NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new BookingStatusNotConfirmedException("Booking must be CONFIRMED", HttpStatus.CONFLICT);
        }

        BigDecimal chargeAmount = switch (booking.getPaymentPlan()) {
            case PREPAYMENT -> booking.getPrepaymentAmount();  // 30%
            case FULL -> booking.getTotalAmount();        // 100%
            case NO_PREPAY -> {
                throw new BookingStatusNotConfirmedException(
                    "This booking has NO_PREPAY plan — payment at check-in",
                    HttpStatus.CONFLICT);
            }
        };

        PaymentHistoryEntity paymentHistory = new PaymentHistoryEntity();
        paymentHistory.setBooking(booking);
        paymentHistory.setType(PaymentHistoryPaymentType.CHARGE);
        paymentHistory.setAmount(chargeAmount);
        paymentHistory.setStatus(PaymentHistoryPaymentStatus.PENDING);
        paymentHistory.setCreatedAt(LocalDateTime.now());
        paymentHistoryRepository.save(paymentHistory);

        bookingKafkaProducer.sendBookingPayment(paymentHistory, booking, paymentRequestDto.currency());

        log.info("Payment queued: bookingId={}, paymentId={}", bookingId, paymentHistory.getId());
        return paymentMapper.toResponse(paymentHistory);
    }

    public PaymentHistoryResponseDto refund(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found", HttpStatus.NOT_FOUND));

        if (!booking.getStatus().equals(BookingStatus.PAID)) {
            throw new BookingStatusNotConfirmedException("Booking must be PAID for refund", HttpStatus.CONFLICT);
        }

        if (booking.getPaymentPlan() == PaymentPlan.NO_PREPAY) {
            throw new BookingStatusNotConfirmedException(
                "NO_PREPAY bookings cannot be refunded — nothing was charged",
                HttpStatus.CONFLICT);
        }

        BigDecimal refundAmount = calculateRefundAmount(booking);

        if (refundAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BookingStatusNotConfirmedException(
                "Refund amount is 0 , cancellation deadline has passed",
                HttpStatus.CONFLICT);
        }

        PaymentHistoryEntity refund = new PaymentHistoryEntity();
        refund.setBooking(booking);
        refund.setType(PaymentHistoryPaymentType.REFUND);
        refund.setAmount(refundAmount);
        refund.setStatus(PaymentHistoryPaymentStatus.PENDING);
        refund.setCreatedAt(LocalDateTime.now());
        paymentHistoryRepository.save(refund);

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        BookingHistoryEntity bookingHistory = BookingHistoryEntity.builder()
            .booking(booking)
            .statusFrom(previousStatus)
            .statusTo(BookingStatus.CANCELLED)
            .changedAt(LocalDateTime.now())
            .changedBy(BookingChangeSource.USER)
            .comment("Booking cancelled with refund")
            .build();
        bookingHistoryRepository.save(bookingHistory);

        bookingKafkaProducer.sendBookingRefund(refund, booking);

        log.info("Refund queued: bookingId={}, refundId={}", bookingId, refund.getId());
        return paymentMapper.toResponse(refund);
    }


    private BigDecimal calculateRefundAmount(BookingEntity booking) {
        var policy = booking.getCancellationPolicy();
        long hoursUntilCheckIn = ChronoUnit.HOURS.between(
            LocalDateTime.now(),
            booking.getCheckInDate().atStartOfDay()
        );

        BigDecimal chargedAmount = switch (booking.getPaymentPlan()) {
            case PREPAYMENT -> booking.getPrepaymentAmount(); // 30%
            case FULL -> booking.getTotalAmount(); // 100%
            case NO_PREPAY -> BigDecimal.ZERO;
        };


        if (hoursUntilCheckIn >= policy.getHoursBeforeCheckIn()) {
            return chargedAmount
                .multiply(policy.getRefundPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }
}
