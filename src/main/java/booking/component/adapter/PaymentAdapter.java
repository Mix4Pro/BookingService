package booking.component.adapter;

import booking.component.adapter.property.PaymentProperties;
import booking.constant.enums.CurrencyType;
import booking.constant.enums.PaymentStatus;
import booking.constant.enums.TransactionType;
import booking.dto.request.PaymentRequestDto;
import booking.dto.response.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentAdapter {
    //    private final RestClient restClient;
    private final PaymentProperties paymentProperties;

    public PaymentResponseDto charge(
            BigDecimal amount,
            CurrencyType currency,
            String senderName,
            String senderToken
    ) {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                UUID.randomUUID(),
                TransactionType.PAYMENT,
                CurrencyType.USD,
                UUID.fromString(paymentProperties.getMerchantId()),
                senderName,
                senderToken,
                "HoYo Booking Service",
                paymentProperties.getMerchantToken()
        );

        return sendTransaction(amount, paymentRequestDto);
    }

    public PaymentResponseDto refund(
            BigDecimal amount,
            CurrencyType currency,
            String receiverName,
            String receiverToken
    ) {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                UUID.randomUUID(),
                TransactionType.PAYMENT,
                CurrencyType.USD,
                UUID.fromString(paymentProperties.getMerchantId()),
                "Booking Service",
                paymentProperties.getMerchantToken(),
                receiverName,
                receiverToken
        );

        return sendTransaction(amount, paymentRequestDto);
    }


    public PaymentResponseDto sendTransaction(BigDecimal amount, PaymentRequestDto paymentRequestDto) {
        return new PaymentResponseDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                PaymentStatus.COMPLETED,
                amount,
                paymentRequestDto.currency(),
                LocalDateTime.now()
        );
    }
}
