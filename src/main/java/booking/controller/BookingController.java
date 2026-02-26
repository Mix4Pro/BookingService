package booking.controller;

import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hold")
    public ResponseEntity<BookingResponseDto> holdARoom (@RequestBody @Valid BookingRequestDto bookingRequestDto) {
        return ResponseEntity.status(201).body(bookingService.holdARoom(bookingRequestDto));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDto> confirmARoom (@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmARoom(id));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponseDto> cancelABooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancel(bookingId));
    }
}
