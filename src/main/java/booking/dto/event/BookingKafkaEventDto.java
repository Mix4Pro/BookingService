package booking.dto.event;

import java.time.LocalDateTime;

public record BookingKafkaEventDto(
    long bookingId,
    long roomId,
    Double price,
    LocalDateTime createdAt
) {
}
