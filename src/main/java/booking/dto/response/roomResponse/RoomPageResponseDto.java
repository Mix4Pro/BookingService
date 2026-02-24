package booking.dto.response.roomResponse;

import java.util.List;

public record RoomPageResponseDto(
    List<RoomResponseDto> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
}
