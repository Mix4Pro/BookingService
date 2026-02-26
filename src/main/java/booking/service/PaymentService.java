package booking.service;

import booking.dto.request.PaymentRequestDto;
import booking.dto.response.PaymentHistoryResponseDto;
import org.jspecify.annotations.Nullable;

public interface PaymentService {
    PaymentHistoryResponseDto charge(Long bookingId, PaymentRequestDto request);

    PaymentHistoryResponseDto refund(Long bookingId);
}
