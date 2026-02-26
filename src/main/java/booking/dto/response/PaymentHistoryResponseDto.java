package booking.dto.response;

import booking.constant.enums.PaymentHistoryPaymentStatus;
import booking.constant.enums.PaymentHistoryPaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentHistoryResponseDto (
    Long id,
    Long bookingId,
    PaymentHistoryPaymentType type,
    BigDecimal amount,
    PaymentHistoryPaymentStatus status,
    UUID bankTransactionId,
    LocalDateTime createdAt

) {

}
