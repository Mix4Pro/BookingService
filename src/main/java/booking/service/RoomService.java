package booking.service;

import booking.dto.request.RoomSearchRequestDto;
import booking.dto.response.roomResponse.RoomPageResponseDto;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    RoomPageResponseDto getAllRooms(Pageable pageable);

    RoomPageResponseDto getSortedRooms(Pageable pageable, RoomSearchRequestDto roomSearchRequestDto);
}
