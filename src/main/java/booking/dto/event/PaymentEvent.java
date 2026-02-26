package booking.dto.event;

import booking.constant.enums.CurrencyType;
import booking.constant.enums.PaymentType;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentEvent (
    Long paymentId,
    Long bookingId,
    String senderName,
    String senderToken,
    BigDecimal amount,
    CurrencyType currency,
    PaymentType type
) {

}
