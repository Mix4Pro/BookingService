package booking.dto.response.bookingResponse;

import java.util.List;

public record BookingPageResponseDto(
    List<BookingResponseDto> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
}
