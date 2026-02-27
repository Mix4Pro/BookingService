package booking.dto.response.bookingResponse;


import java.time.LocalDate;

public record BookingResponseDto(
    Long id,
    Long userId,
    Long roomId,
    Long rateId,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    Integer guestsCount,
    Long cancellationPolicyId
) {}
