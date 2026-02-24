package booking.service.roomService;

import booking.dto.request.RoomRequestDto;
import booking.dto.response.roomResponse.RoomPageResponseDto;
import booking.dto.response.roomResponse.RoomResponseDto;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    RoomResponseDto createRoom(RoomRequestDto roomRequestDto);
    RoomResponseDto getRoomById(long id);
    RoomPageResponseDto getAllRooms (Pageable pageable);
}
