package booking.controller;

import booking.constant.enums.BookingStatus;
import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingPageResponseDto;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.service.bookingService.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking/api/v1/booking")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking (
        @RequestBody @Valid BookingRequestDto bookingRequestDto
    ) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequestDto));
    }

    @PatchMapping("/confirm/{id}")
    public ResponseEntity<BookingResponseDto> confirmBooking (
        @PathVariable
        @Min(value = 1, message = "Id can't be less than 1")
        long id
    ){
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<BookingResponseDto> cancelBooking (
        @PathVariable
        @Min(value = 1, message = "Id can't be less than 1")
        long id
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/get-by-status")
    public ResponseEntity<BookingPageResponseDto> findBookingsByStatus (
        @NotNull(message = "Status can't be null")
        @RequestParam BookingStatus status,
        Pageable pageable
    ) {
        return ResponseEntity.ok(bookingService.findBookingsByStatus(status,pageable));
    }
}
