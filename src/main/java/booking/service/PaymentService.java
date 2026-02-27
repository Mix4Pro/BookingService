package booking.service;

import booking.dto.request.PaymentRequestDto;
import booking.dto.response.PaymentHistoryResponseDto;

public interface PaymentService {
    PaymentHistoryResponseDto charge(Long bookingId, PaymentRequestDto request);

    PaymentHistoryResponseDto refund(Long bookingId);
}
