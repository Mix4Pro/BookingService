package booking.controller;

import booking.dto.request.PaymentRequestDto;
import booking.dto.response.PaymentHistoryResponseDto;
import booking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{bookingId}/pay")
    public ResponseEntity<PaymentHistoryResponseDto> charge(
        @PathVariable Long bookingId,
        @RequestBody @Valid PaymentRequestDto paymentRequestDto
    ) {
        return ResponseEntity.ok(paymentService.charge(bookingId, paymentRequestDto));
    }

    @PostMapping("/{bookingId}/refund")
    public ResponseEntity<PaymentHistoryResponseDto> refund(
        @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(paymentService.refund(bookingId));
    }
}
