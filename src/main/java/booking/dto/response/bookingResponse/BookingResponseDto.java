package booking.dto.response.bookingResponse;

import booking.constant.enums.BookingStatus;

import java.time.LocalDate;

public record BookingResponseDto(
    long id,
    Double price,
    LocalDate checkIn,
    LocalDate checkOut,
    BookingStatus status,
    Long roomId
) {}
