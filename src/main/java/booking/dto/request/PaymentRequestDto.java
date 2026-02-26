package booking.dto.request;

import booking.constant.enums.CurrencyType;
import booking.constant.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record PaymentRequestDto(
    @NotNull(message = "referenceId can't be null")
    UUID referenceId,

    @NotNull(message = "type can't be null")
    TransactionType type,

    @NotNull(message = "currency can't be null")
    CurrencyType currency,

    @NotNull(message = "merchatId can't be null")
    UUID merchantId,

    @NotNull(message = "senderName can't be null")
    String senderName,

    @NotNull(message = "senderToken can't be null")
    String senderToken,

    @NotNull(message = "receiverName can't be null")
    String receiverName,

    @NotNull(message = "Receiver can't be null")
    String receiverToken
) {
}
