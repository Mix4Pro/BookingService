package booking.dto.response;

import booking.constant.enums.CurrencyType;
import booking.constant.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(
    UUID id,
    UUID referenceId,
    PaymentStatus status,
    BigDecimal amount,
    CurrencyType currency,
    LocalDateTime createdAt
) {
}
